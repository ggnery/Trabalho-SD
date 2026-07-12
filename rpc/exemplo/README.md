# Exemplo gRPC — Fibonacci (cliente Python ↔ servidor Go)

Exemplo **mínimo** de RPC: o cliente em **Python** chama `fib(n)` e o servidor em **Go**
calcula o N-ésimo termo de Fibonacci. A chamada parece local, mas executa na outra
linguagem/processo — é a *transparência* da RPC.

```
  Python (cliente)                     Go (servidor)
  stub.fib(N(n=10))  ──requisição──▶   fib(10)=55
                     ◀──resposta────   V{v:55}
      Protocol Buffers + HTTP/2 (porta 50051)
```

Contrato (`fibonacci.proto`): `service Calc { rpc fib(N) returns (V); }`.

## Estrutura

```
exemplo/
├── fibonacci.proto        # contrato (service Calc, rpc fib)
├── gerar_stubs.sh         # gera os stubs das 2 linguagens
├── servidor/main.go       # servidor Go
└── cliente/cliente.py     # cliente Python
```

## Pré-requisitos

- **Go** 1.21+ · **Python** 3.8+ · **protoc** (`brew install protobuf` ou `apt install protobuf-compiler`)

## Passo a passo (na pasta `exemplo/`)

```bash
# 1. plugins/bibliotecas (só na 1ª vez)
go install google.golang.org/protobuf/cmd/protoc-gen-go@latest
go install google.golang.org/grpc/cmd/protoc-gen-go-grpc@latest
export PATH="$PATH:$(go env GOPATH)/bin"
pip install -r cliente/requirements.txt

# 2. gerar os stubs a partir do .proto
./gerar_stubs.sh

# 3. servidor (Terminal 1)
cd servidor && go mod tidy && go run .

# 4. cliente (Terminal 2)
cd cliente && python cliente.py 10     # -> 55
                python cliente.py 20    # -> 6765
```

`./gerar_stubs.sh` cria `servidor/pb/*.go` e `cliente/fibonacci_pb2*.py`.

## Ligação com o seminário

| Conceito do slide | No exemplo |
|---|---|
| Interface / IDL | `fibonacci.proto` (`service Calc`, `rpc fib`) |
| Stub do cliente | `g.CalcStub(...).fib(...)` em `cliente.py` |
| Stub/despachante do servidor | `pb.RegisterCalcServer` + método `Fib` em Go |
| Marshalling | Protocol Buffers serializa `N`/`V` |
| Transporte | HTTP/2 na porta 50051 |
| Multi-linguagem | cliente Python × servidor Go, mesmo `.proto` |

> `int64` vai até `fib(92)`; acima disso há overflow.
