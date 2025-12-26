package com.ebank.soap;

import com.ebank.dto.AccountDTO;
import com.ebank.dto.CreateAccountRequest;
import com.ebank.service.AccountService;
import com.ebank.soap.generated.*;
import lombok.RequiredArgsConstructor;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.GregorianCalendar;

@Endpoint
@RequiredArgsConstructor
public class AccountEndpoint {

    private static final String NAMESPACE_URI = "http://ebank.com/soap/accounts";

    private final AccountService accountService;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAccountByRibRequest")
    @ResponsePayload
    public GetAccountByRibResponse getAccountByRib(@RequestPayload GetAccountByRibRequest request) {
        AccountDTO accountDTO = accountService.getAccountByRib(request.getRib());

        GetAccountByRibResponse response = new GetAccountByRibResponse();
        response.setAccount(mapToAccountType(accountDTO));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getAccountByIdRequest")
    @ResponsePayload
    public GetAccountByIdResponse getAccountById(@RequestPayload GetAccountByIdRequest request) {
        AccountDTO accountDTO = accountService.getAccountById(request.getId());

        GetAccountByIdResponse response = new GetAccountByIdResponse();
        response.setAccount(mapToAccountType(accountDTO));
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createAccountRequest")
    @ResponsePayload
    public CreateAccountResponse createAccount(@RequestPayload com.ebank.soap.generated.CreateAccountRequest request) {
        CreateAccountRequest serviceRequest = new CreateAccountRequest();
        serviceRequest.setRib(request.getRib());
        serviceRequest.setIdentityNumber(request.getIdentityNumber());

        AccountDTO accountDTO = accountService.createAccount(serviceRequest);

        CreateAccountResponse response = new CreateAccountResponse();
        response.setAccount(mapToAccountType(accountDTO));
        return response;
    }

    private AccountType mapToAccountType(AccountDTO dto) {
        AccountType accountType = new AccountType();
        accountType.setId(dto.getId());
        accountType.setRib(dto.getRib());
        accountType.setBalance(dto.getBalance());
        accountType.setStatus(dto.getStatus());
        accountType.setCreatedAt(toXMLGregorianCalendar(dto.getCreatedAt()));
        accountType.setClientName(dto.getClientName());
        return accountType;
    }

    private XMLGregorianCalendar toXMLGregorianCalendar(LocalDateTime localDateTime) {
        try {
            GregorianCalendar gregorianCalendar = GregorianCalendar.from(
                    localDateTime.atZone(ZoneId.systemDefault()));
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error converting date", e);
        }
    }
}
