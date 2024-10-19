package org.example.bidirectional;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import com.iiith.assignment.model.sec06.ChatMessage;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BidirectionalClient {

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
                .usePlaintext()
                .build();

        com.iiith.assignment.model.sec06.BidirectionalServiceGrpc.BidirectionalServiceStub asyncStub = com.iiith.assignment.model.sec06.BidirectionalServiceGrpc.newStub(channel);

        CountDownLatch latch = new CountDownLatch(1);

        // StreamObserver for receiving responses from the server
        StreamObserver<ChatMessage> responseObserver = new StreamObserver<ChatMessage>() {

            @Override
            public void onNext(ChatMessage value) {
                System.out.println("Received from server: " + value.getUser() + ": " + value.getMessage());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                System.out.println("Server has completed sending messages.");
                latch.countDown();
            }
        };

        // StreamObserver for sending messages to the server
        StreamObserver<ChatMessage> requestObserver = asyncStub.chat(responseObserver);

        // Send multiple messages
        for (int i = 0; i < 5; i++) {
            ChatMessage message = ChatMessage.newBuilder()
                    .setUser("Client " + i)
                    .setMessage("Hello from client " + i)
                    .build();

            requestObserver.onNext(message);

            // Simulate a delay
            Thread.sleep(1000);
        }

        // Mark the end of requests
        requestObserver.onCompleted();

        // Wait for the server to finish
        latch.await(3, TimeUnit.SECONDS);

        channel.shutdown();
    }
}
