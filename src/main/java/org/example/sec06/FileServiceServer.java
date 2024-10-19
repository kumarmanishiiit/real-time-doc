package org.example.sec06;

import com.example.grpc.FileServiceProto;
import com.google.protobuf.ByteString;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileServiceServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(8080)
                .addService(new FileServiceImpl())
                .build();

        System.out.println("Starting server on port 8080...");
        server.start();
        server.awaitTermination();
    }


    static class FileServiceImpl extends com.example.grpc.FileServiceGrpc.FileServiceImplBase {

        @Override
        public StreamObserver<FileServiceProto.FileRequest> syncFile(StreamObserver<FileServiceProto.FileChunk> responseObserver) {

            return  new StreamObserver<>() {

                @Override
                public void onNext(FileServiceProto.FileRequest fileRequest) {
                    ByteString byteString1 = ByteString.copyFromUtf8("Hello ");
                    var response = FileServiceProto.FileChunk.newBuilder()
                            .setSize(2321)
                            .setContent(byteString1).build();
                    responseObserver.onNext(response);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println("Processing completed!!!");
                    responseObserver.onCompleted();
                }
            };
        }

        @Override
        public void downloadFile(FileServiceProto.FileRequest request, StreamObserver<FileServiceProto.FileChunk> responseObserver) {
            String fileName = request.getFileName();
            File file = new File("/Users/manish.kumar2/Desktop/IIITH/Distributed System/grpc/grpc-java/cache/test.txt"); // Modify this path accordingly

            if (!file.exists()) {
                responseObserver.onError(new RuntimeException("File not found"));
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024]; // Send file in 1KB chunks
                int bytesRead;

                 // Read a file and convert its contents to ByteString
                 // ByteString byteString = ByteString.readFrom(Files.newInputStream(Paths.get("example.txt")));
                while ((bytesRead = fis.read(buffer)) != -1) {
                    FileServiceProto.FileChunk chunk = FileServiceProto.FileChunk.newBuilder()
                            .setContent(com.google.protobuf.ByteString.copyFrom(buffer, 0, bytesRead))
                            .setSize(bytesRead)
                            .build();

                    responseObserver.onNext(chunk);
                }
                responseObserver.onCompleted();
            } catch (IOException e) {
                e.printStackTrace();
                responseObserver.onError(e);
            }
        }
    }
}
