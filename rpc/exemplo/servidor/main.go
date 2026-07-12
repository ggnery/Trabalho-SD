package main

import (
	"context"
	"net"

	"google.golang.org/grpc"
	pb "fibexemplo/pb"
)

type srv struct{ pb.UnimplementedCalcServer }

func (srv) Fib(_ context.Context, r *pb.N) (*pb.V, error) {
	a, b := int64(0), int64(1)
	for i := int32(0); i < r.N; i++ {
		a, b = b, a+b
	}
	return &pb.V{V: a}, nil
}

func main() {
	l, _ := net.Listen("tcp", ":50051")
	g := grpc.NewServer()
	pb.RegisterCalcServer(g, srv{})
	g.Serve(l)
}
