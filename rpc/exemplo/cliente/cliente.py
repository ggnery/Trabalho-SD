import sys
import grpc
import fibonacci_pb2 as pb
import fibonacci_pb2_grpc as pbg

n = int(sys.argv[1]) if len(sys.argv) > 1 else 10

canal = grpc.insecure_channel("localhost:50051")
stub = pbg.CalcStub(canal)
resposta = stub.fib(pb.N(n=n))

print(resposta.v)
