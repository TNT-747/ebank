package com.ebank.config;

import com.ebank.entity.*;
import com.ebank.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

        private final UserRepository userRepository;
        private final ClientRepository clientRepository;
        private final AccountRepository accountRepository;
        private final TransactionRepository transactionRepository;
        private final PasswordEncoder passwordEncoder;

        @Override
        public void run(String... args) {
                // Create AGENT_GUICHET user
                if (!userRepository.existsByUsername("agent")) {
                        User agent = User.builder()
                                        .username("agent")
                                        .password(passwordEncoder.encode("agent123"))
                                        .email("agent@kassimibank.com")
                                        .role(Role.AGENT_GUICHET)
                                        .enabled(true)
                                        .build();
                        userRepository.save(agent);
                        log.info("Created AGENT_GUICHET user: agent / agent123");
                }

                // Create sample CLIENT user
                if (!userRepository.existsByUsername("client1")) {
                        User clientUser = User.builder()
                                        .username("client1")
                                        .password(passwordEncoder.encode("client123"))
                                        .email("client1@example.com")
                                        .role(Role.CLIENT)
                                        .enabled(true)
                                        .build();
                        clientUser = userRepository.save(clientUser);

                        Client client = new Client();
                        client.setFirstName("Ahmed");
                        client.setLastName("Kassimi");
                        client.setIdentityNumber("AB123456");
                        client.setBirthDate(LocalDate.of(1990, 5, 15));
                        client.setEmail("client1@example.com");
                        client.setAddress("123 Avenue Mohammed V, Casablanca");
                        client.setUser(clientUser);
                        client = clientRepository.save(client);

                        // Create account with initial balance
                        Account account = Account.builder()
                                        .rib("MA64001128000012345678901234")
                                        .balance(new BigDecimal("5000.00"))
                                        .status(AccountStatus.OPEN)
                                        .createdAt(LocalDateTime.now())
                                        .client(client)
                                        .build();
                        account = accountRepository.save(account);

                        // Create sample transactions
                        transactionRepository.save(Transaction.builder()
                                        .type(TransactionType.CREDIT)
                                        .amount(new BigDecimal("5000.00"))
                                        .label("Dépôt initial")
                                        .date(LocalDateTime.now().minusDays(30))
                                        .account(account)
                                        .build());

                        transactionRepository.save(Transaction.builder()
                                        .type(TransactionType.DEBIT)
                                        .amount(new BigDecimal("500.00"))
                                        .label("Virement émis - Facture électricité")
                                        .date(LocalDateTime.now().minusDays(25))
                                        .account(account)
                                        .build());

                        transactionRepository.save(Transaction.builder()
                                        .type(TransactionType.CREDIT)
                                        .amount(new BigDecimal("2000.00"))
                                        .label("Virement en votre faveur - Salaire")
                                        .date(LocalDateTime.now().minusDays(20))
                                        .account(account)
                                        .build());

                        transactionRepository.save(Transaction.builder()
                                        .type(TransactionType.DEBIT)
                                        .amount(new BigDecimal("150.00"))
                                        .label("Virement émis - Abonnement internet")
                                        .date(LocalDateTime.now().minusDays(15))
                                        .account(account)
                                        .build());

                        transactionRepository.save(Transaction.builder()
                                        .type(TransactionType.CREDIT)
                                        .amount(new BigDecimal("300.00"))
                                        .label("Virement en votre faveur - Remboursement")
                                        .date(LocalDateTime.now().minusDays(10))
                                        .account(account)
                                        .build());

                        log.info("Created CLIENT user: client1 / client123 with account RIB: {}", account.getRib());
                }

                // Create second client for transfers
                if (!userRepository.existsByUsername("client2")) {
                        User clientUser2 = User.builder()
                                        .username("client2")
                                        .password(passwordEncoder.encode("client123"))
                                        .email("client2@example.com")
                                        .role(Role.CLIENT)
                                        .enabled(true)
                                        .build();
                        clientUser2 = userRepository.save(clientUser2);

                        Client client2 = new Client();
                        client2.setFirstName("Sara");
                        client2.setLastName("Benali");
                        client2.setIdentityNumber("CD789012");
                        client2.setBirthDate(LocalDate.of(1992, 8, 20));
                        client2.setEmail("client2@example.com");
                        client2.setAddress("456 Boulevard Zerktouni, Casablanca");
                        client2.setUser(clientUser2);
                        client2 = clientRepository.save(client2);

                        Account account2 = Account.builder()
                                        .rib("MA64001128000098765432109876")
                                        .balance(new BigDecimal("3000.00"))
                                        .status(AccountStatus.OPEN)
                                        .createdAt(LocalDateTime.now())
                                        .client(client2)
                                        .build();
                        accountRepository.save(account2);

                        log.info("Created CLIENT user: client2 / client123 with account RIB: {}", account2.getRib());
                }

                log.info("========================================");
                log.info("DATA INITIALIZATION COMPLETE");
                log.info("========================================");
                log.info("Test Credentials:");
                log.info("  AGENT: agent / agent123");
                log.info("  CLIENT 1: client1 / client123");
                log.info("  CLIENT 2: client2 / client123");
                log.info("========================================");
        }
}
