package org.example.sec06;

import com.example.grpc.FileServiceGrpc;
import com.example.grpc.FileServiceProto;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.io.FileOutputStream;
import java.io.IOException;

public class FileServiceClient {

    public static void main(String[] args) throws IOException {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        FileServiceGrpc.FileServiceStub stub = FileServiceGrpc.newStub(channel);

        FileServiceProto.FileRequest request = FileServiceProto.FileRequest.newBuilder()
                .setFileName("example.txt")  // Replace with your file name
                .build();

        stub.downloadFile(request, new StreamObserver<FileServiceProto.FileChunk>() {
            FileOutputStream fos = new FileOutputStream("cache/download.txt"); // Destination path

            @Override
            public void onNext(FileServiceProto.FileChunk fileChunk) {
                try {
                    fos.write(fileChunk.getContent().toByteArray());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCompleted() {
                System.out.println("File downloaded successfully.");
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // Wait for the server to finish sending the file
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        channel.shutdown();
    }

//    public static void main(String[] args) throws IOException {
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
//                .usePlaintext()
//                .build();
//
//        // Example to sync the file:
//        com.example.grpc.FileServiceGrpc.FileServiceStub stub = com.example.grpc.FileServiceGrpc.newStub(channel);
//
//        FileServiceProto.FileRequest request = FileServiceProto.FileRequest.newBuilder()
//                .setFileName("example.txt")  // Replace with your file name
//                .build();
//
////        stub.downloadFile();
//        var requestObserver = stub.syncFile(new StreamObserver<FileServiceProto.FileChunk>() {
//            FileOutputStream fos = new FileOutputStream("cache/download"); // Destination path
//
//            @Override
//            public void onNext(FileServiceProto.FileChunk fileChunk) {
//                System.out.println(fileChunk.getContent());
//                System.out.println(fileChunk.getSize());
//            }
//
//            @Override
//            public void onError(Throwable t) {
//                t.printStackTrace();
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onCompleted() {
//                System.out.println("File downloaded successfully.");
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//        requestObserver.onNext(request);
//
////
////        FileServiceProto.FileRequest request2 = FileServiceProto.FileRequest.newBuilder()
////                .setFileName("example1.txt")  // Replace with your file name
////                .build();
////
////        requestObserver.onNext(request2);
//
//        requestObserver.onCompleted();
//
//        // Wait for the server to finish sending the file
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        channel.shutdown();
//    }
}

