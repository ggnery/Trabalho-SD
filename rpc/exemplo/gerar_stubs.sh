#!/usr/bin/env bash
set -e
cd "$(dirname "$0")"

command -v protoc >/dev/null || {
  echo "ERRO: 'protoc' nao encontrado.  Instale:  brew install protobuf"; exit 1; }
command -v protoc-gen-go >/dev/null || {
  echo "ERRO: plugins Go do protoc nao encontrados.  Instale:"
  echo "  go install google.golang.org/protobuf/cmd/protoc-gen-go@latest"
  echo "  go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest"
  echo "  export PATH=\"\$PATH:\$(go env GOPATH)/bin\""; exit 1; }
python3 -c "import grpc_tools" 2>/dev/null || {
  echo "ERRO: grpcio-tools nao encontrado.  Instale:  pip3 install grpcio grpcio-tools"; exit 1; }

echo ">> Gerando stubs Go em servidor/pb ..."
protoc -I. \
    --go_out=servidor --go_opt=module=fibexemplo \
    --go-grpc_out=servidor --go-grpc_opt=module=fibexemplo \
    fibonacci.proto

echo ">> Gerando stubs Python em cliente/ ..."
python3 -m grpc_tools.protoc -I. \
    --python_out=cliente --grpc_python_out=cliente \
    fibonacci.proto

echo ">> Pronto. Agora, em dois terminais:"
echo "   Terminal 1:  cd servidor && go mod tidy && go run ."
echo "   Terminal 2:  cd cliente  && python3 cliente.py 10"
