package com.ebank.graphql;

import com.ebank.dto.AccountDTO;
import com.ebank.dto.CreateAccountRequest;
import com.ebank.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AccountGraphQLController {

    private final AccountService accountService;

    @QueryMapping
    public AccountDTO getAccountById(@Argument Long id) {
        return accountService.getAccountById(id);
    }

    @QueryMapping
    public AccountDTO getAccountByRib(@Argument String rib) {
        return accountService.getAccountByRib(rib);
    }

    @QueryMapping
    public List<AccountDTO> getAccountsByClientId(@Argument Long clientId) {
        return accountService.getAccountsByClientId(clientId);
    }

    @MutationMapping
    public AccountDTO createAccount(@Argument CreateAccountInput input) {
        CreateAccountRequest request = new CreateAccountRequest();
        request.setRib(input.getRib());
        request.setIdentityNumber(input.getIdentityNumber());
        return accountService.createAccount(request);
    }
}
