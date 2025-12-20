package com.ebank.service;

import com.ebank.dto.AccountDTO;
import com.ebank.dto.DashboardDTO;
import com.ebank.dto.TransactionDTO;
import com.ebank.entity.Client;
import com.ebank.entity.User;
import com.ebank.exception.BusinessException;
import com.ebank.repository.ClientRepository;
import com.ebank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public DashboardDTO getDashboard(String username, Long selectedAccountId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        Client client = clientRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BusinessException("Client non trouvé"));

        List<AccountDTO> allAccounts = accountService.getAccountsByClientId(client.getId());

        if (allAccounts.isEmpty()) {
            return DashboardDTO.builder()
                    .allAccounts(allAccounts)
                    .totalBalance(BigDecimal.ZERO)
                    .build();
        }

        // Get selected account or first account by default
        AccountDTO selectedAccount;
        if (selectedAccountId != null) {
            selectedAccount = allAccounts.stream()
                    .filter(a -> a.getId().equals(selectedAccountId))
                    .findFirst()
                    .orElse(allAccounts.get(0));
        } else {
            selectedAccount = allAccounts.get(0);
        }

        // Get last 10 transactions for selected account
        List<TransactionDTO> recentTransactions = transactionService
                .getTop10TransactionsByAccountId(selectedAccount.getId());

        // Calculate total balance
        BigDecimal totalBalance = allAccounts.stream()
                .map(AccountDTO::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return DashboardDTO.builder()
                .account(selectedAccount)
                .recentTransactions(recentTransactions)
                .allAccounts(allAccounts)
                .totalBalance(totalBalance)
                .build();
    }
}
