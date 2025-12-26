package com.ebank.grpc;

import com.ebank.dto.AccountDTO;
import com.ebank.dto.CreateAccountRequest;
import com.ebank.exception.ResourceNotFoundException;
import com.ebank.grpc.generated.*;
import com.ebank.service.AccountService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.format.DateTimeFormatter;

@GrpcService
@RequiredArgsConstructor
public class AccountGrpcServiceImpl extends AccountGrpcServiceGrpc.AccountGrpcServiceImplBase {

    private final AccountService accountService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public void getAccountByRib(GetAccountByRibRequest request, StreamObserver<AccountResponse> responseObserver) {
        try {
            AccountDTO accountDTO = accountService.getAccountByRib(request.getRib());
            AccountResponse response = mapToAccountResponse(accountDTO);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void getAccountById(GetAccountByIdRequest request, StreamObserver<AccountResponse> responseObserver) {
        try {
            AccountDTO accountDTO = accountService.getAccountById(request.getId());
            AccountResponse response = mapToAccountResponse(accountDTO);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (ResourceNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL
                    .withDescription("Internal server error: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    @Override
    public void createAccount(CreateAccountGrpcRequest request, StreamObserver<AccountResponse> responseObserver) {
        try {
            CreateAccountRequest serviceRequest = new CreateAccountRequest();
            serviceRequest.setRib(request.getRib());
            serviceRequest.setIdentityNumber(request.getIdentityNumber());

            AccountDTO accountDTO = accountService.createAccount(serviceRequest);
            AccountResponse response = mapToAccountResponse(accountDTO);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                    .withDescription("Failed to create account: " + e.getMessage())
                    .asRuntimeException());
        }
    }

    private AccountResponse mapToAccountResponse(AccountDTO dto) {
        return AccountResponse.newBuilder()
                .setId(dto.getId())
                .setRib(dto.getRib())
                .setBalance(dto.getBalance().toString())
                .setStatus(dto.getStatus())
                .setCreatedAt(dto.getCreatedAt().format(DATE_FORMATTER))
                .setClientName(dto.getClientName())
                .build();
    }
}
