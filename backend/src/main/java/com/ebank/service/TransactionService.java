package com.ebank.service;

import com.ebank.dto.TransactionDTO;
import com.ebank.dto.TransferRequest;
import com.ebank.entity.Account;
import com.ebank.entity.AccountStatus;
import com.ebank.entity.Transaction;
import com.ebank.entity.TransactionType;
import com.ebank.exception.BusinessException;
import com.ebank.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public Page<TransactionDTO> getTransactionsByAccountId(Long accountId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return transactionRepository.findByAccountIdOrderByDateDesc(accountId, pageable)
                .map(this::mapToDTO);
    }

    public List<TransactionDTO> getTop10TransactionsByAccountId(Long accountId) {
        return transactionRepository.findTop10ByAccountIdOrderByDateDesc(accountId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void executeTransfer(TransferRequest request, String username) {
        Account sourceAccount = accountService.getAccountEntityByRib(request.getSourceRib());
        Account destinationAccount = accountService.getAccountEntityByRib(request.getDestinationRib());

        // RG_11: Account must not be blocked or closed
        if (sourceAccount.getStatus() != AccountStatus.OPEN) {
            throw new BusinessException("Le compte bancaire est bloqué ou clôturé");
        }

        if (destinationAccount.getStatus() != AccountStatus.OPEN) {
            throw new BusinessException("Le compte destinataire est bloqué ou clôturé");
        }

        // RG_12: Balance must be greater than transfer amount
        if (sourceAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new BusinessException("Le solde de compte doit être supérieur au montant du virement");
        }

        // Cannot transfer to same account
        if (sourceAccount.getRib().equals(destinationAccount.getRib())) {
            throw new BusinessException("Impossible d'effectuer un virement vers le même compte");
        }

        LocalDateTime now = LocalDateTime.now();

        // RG_13: Debit source account
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        accountService.updateBalance(sourceAccount);

        // RG_14: Credit destination account
        destinationAccount.setBalance(destinationAccount.getBalance().add(request.getAmount()));
        accountService.updateBalance(destinationAccount);

        // RG_15: Create DEBIT transaction for source
        Transaction debitTransaction = Transaction.builder()
                .type(TransactionType.DEBIT)
                .amount(request.getAmount())
                .label("Virement émis vers " + destinationAccount.getRib() + " - " + request.getMotif())
                .date(now)
                .account(sourceAccount)
                .build();
        transactionRepository.save(debitTransaction);

        // RG_15: Create CREDIT transaction for destination
        Transaction creditTransaction = Transaction.builder()
                .type(TransactionType.CREDIT)
                .amount(request.getAmount())
                .label("Virement en votre faveur de " + sourceAccount.getRib() + " - " + request.getMotif())
                .date(now)
                .account(destinationAccount)
                .build();
        transactionRepository.save(creditTransaction);
    }

    private TransactionDTO mapToDTO(Transaction transaction) {
        return TransactionDTO.builder()
                .id(transaction.getId())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .label(transaction.getLabel())
                .date(transaction.getDate())
                .build();
    }
}
