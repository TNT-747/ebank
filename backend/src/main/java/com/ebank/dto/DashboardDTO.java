package com.ebank.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {
    private AccountDTO account;
    private List<TransactionDTO> recentTransactions;
    private List<AccountDTO> allAccounts;
    private BigDecimal totalBalance;
}
