package org.example.common;


import org.example.sec06.BankService;
import org.example.sec06.KNNService;

/*
    a simple class to start the server with specific services for demo purposes
 */
public class DemoKNN {

    public static void main(String[] args) {

        GrpcServer server1 = GrpcServer.create(6565, builder -> {
                    builder.addService(new KNNService());
                });

        GrpcServer server2 = GrpcServer.create(7575, builder -> {
                    builder.addService(new KNNService());
                });

        server1.start();
        server2.start();

        server1.await();
        server2.await();
    }

    private static class BankServerInstance1 {
        public static void main(String[] args) {
            GrpcServer.create(6565, builder -> {
                        builder.addService(new KNNService());
                    })
                    .start()
                    .await();
        }
    }

    private static class BankServerInstance2 {
        public static void main(String[] args) {
            GrpcServer.create(7575, builder -> {
                        builder.addService(new KNNService());
                    })
                    .start()
                    .await();
        }
    }



    /*  Created for load balancing demo
    private static class BankInstance1 {
        public static void main(String[] args) {
            GrpcServer.create(6565, new BankService())
                      .start()
                      .await();
        }
    }

    private static class BankInstance2 {
        public static void main(String[] args) {
            GrpcServer.create(7575, new BankService())
                      .start()
                      .await();
        }
    }
    */
}