package com.ebank.repository;

import com.ebank.entity.Account;
import com.ebank.entity.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByRib(String rib);

    List<Account> findByClientId(Long clientId);

    List<Account> findByClientIdAndStatus(Long clientId, AccountStatus status);

    boolean existsByRib(String rib);

    @Query("SELECT a FROM Account a WHERE a.client.id = :clientId ORDER BY a.createdAt DESC")
    List<Account> findByClientIdOrderByCreatedAtDesc(@Param("clientId") Long clientId);

    @Query("SELECT a FROM Account a WHERE a.client.user.id = :userId")
    List<Account> findByUserId(@Param("userId") Long userId);
}
