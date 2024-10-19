package org.example.bidirectional;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;

public class BidirectionalServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(9090)
                .addService(new BidirectionalServiceImpl())
                .build();

        server.start();
        System.out.println("Server started on port 9090");

        server.awaitTermination();
    }

    static class BidirectionalServiceImpl extends com.iiith.assignment.model.sec06.BidirectionalServiceGrpc.BidirectionalServiceImplBase {

        @Override
        public StreamObserver<com.iiith.assignment.model.sec06.ChatMessage> chat(StreamObserver<com.iiith.assignment.model.sec06.ChatMessage> responseObserver) {
            return new StreamObserver<com.iiith.assignment.model.sec06.ChatMessage>() {

                @Override
                public void onNext(com.iiith.assignment.model.sec06.ChatMessage request) {
                    // Handle the incoming message and send a response
                    System.out.println("Received from client: " + request.getUser() + ": " + request.getMessage());

                    // Save the file back to disk.


                    // Respond back
                    com.iiith.assignment.model.sec06.ChatMessage reply = com.iiith.assignment.model.sec06.ChatMessage.newBuilder()
                            .setUser("Server")
                            .setMessage("Hello " + request.getUser() + ", I got your message!")
                            .build();

                    responseObserver.onNext(reply);
                }

                @Override
                public void onError(Throwable t) {
                    t.printStackTrace();
                }

                @Override
                public void onCompleted() {
                    responseObserver.onCompleted();
                }
            };
        }
    }
}
