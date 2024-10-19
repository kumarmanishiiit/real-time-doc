package org.example.sec06;

import com.google.protobuf.Empty;
import com.iiith.assignment.model.sec06.AccountBalance;
import com.iiith.assignment.model.sec06.AllAccountsResponse;
import com.iiith.assignment.model.sec06.BalanceCheckRequest;
import com.iiith.assignment.model.sec06.BankServiceGrpc;
import io.grpc.stub.StreamObserver;

import com.google.common.util.concurrent.Uninterruptibles;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import org.example.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class BankService extends BankServiceGrpc.BankServiceImplBase {

    private static final Logger log = LoggerFactory.getLogger(BankService.class);

    @Override
    public void getAccountBalance(BalanceCheckRequest request, StreamObserver<AccountBalance> responseObserver) {
        log.info("request received {}", request.getAccountNumber());
        var accountNumber = request.getAccountNumber();
        var balance = AccountRepository.getBalance(accountNumber);
        var accountBalance = AccountBalance.newBuilder()
                .setAccountNumber(accountNumber)
                .setBalance(balance + 1000)
                .build();
        responseObserver.onNext(accountBalance);
        responseObserver.onCompleted();
    }

}
