package org.example.common;

import io.grpc.*;
import io.grpc.netty.shaded.io.grpc.netty.NettyServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

public class KnnGrpcServer {

    private static final Logger log = LoggerFactory.getLogger(KnnGrpcServer.class);

    private final Server server;

    private KnnGrpcServer(Server server){
        this.server = server;
    }

    public static KnnGrpcServer create(BindableService... services){
        return create(6565, services);
    }

    public static KnnGrpcServer create(int port, BindableService... services){
        return create(port, builder -> {
            Arrays.asList(services).forEach(builder::addService);
        });
    }

    public static KnnGrpcServer create(int port, Consumer<NettyServerBuilder> consumer){
        var builder = ServerBuilder.forPort(port);
        consumer.accept((NettyServerBuilder) builder);
        return new KnnGrpcServer(builder.build());
    }

    public KnnGrpcServer start(){
        var services = server.getServices()
                .stream()
                .map(ServerServiceDefinition::getServiceDescriptor)
                .map(ServiceDescriptor::getName);

        try {
            server.start();
            log.info("server started. listening on port {}. services: {}", server.getPort(), services);
            return this;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void await(){
        try{
            server.awaitTermination();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public void stop(){
        server.shutdownNow();
        log.info("server stopped");
    }

}