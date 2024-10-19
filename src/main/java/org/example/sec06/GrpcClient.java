package org.example.sec06;

import com.iiith.assignment.model.sec06.AccountBalance;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcClient {

    public static void main(String[] args) throws InterruptedException {
        var channel = ManagedChannelBuilder.forAddress("localhost", 6565).usePlaintext().build();

        var stub = com.iiith.assignment.model.sec06.BankServiceGrpc.newStub(channel);

        stub.getAccountBalance(com.iiith.assignment.model.sec06.BalanceCheckRequest.newBuilder().setAccountNumber(1).build(), new StreamObserver<AccountBalance>() {
            @Override
            public void onNext(AccountBalance accountBalance) {
                System.out.println(accountBalance.getBalance());
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onCompleted() {

            }
        });

        Thread.sleep(1000);
    }
}
