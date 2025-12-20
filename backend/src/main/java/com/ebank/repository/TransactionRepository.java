package com.ebank.repository;

import com.ebank.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByAccountIdOrderByDateDesc(Long accountId, Pageable pageable);

    List<Transaction> findTop10ByAccountIdOrderByDateDesc(Long accountId);

    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId ORDER BY t.date DESC")
    Page<Transaction> findTransactionsByAccountId(@Param("accountId") Long accountId, Pageable pageable);
}
