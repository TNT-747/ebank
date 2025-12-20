package com.ebank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Value("${mail.mock.enabled:true}")
    private boolean mockEnabled;

    public void sendCredentials(String email, String username, String password) {
        if (mockEnabled) {
            log.info("========================================");
            log.info("MOCK EMAIL - Credentials for new client");
            log.info("To: {}", email);
            log.info("Subject: Bienvenue chez Kassimi Bank - Vos identifiants");
            log.info("----------------------------------------");
            log.info("Bonjour,");
            log.info("");
            log.info("Bienvenue chez Kassimi Bank!");
            log.info("Voici vos identifiants de connexion:");
            log.info("Username: {}", username);
            log.info("Password: {}", password);
            log.info("");
            log.info("Veuillez changer votre mot de passe après votre première connexion.");
            log.info("");
            log.info("Cordialement,");
            log.info("Kassimi Bank");
            log.info("========================================");
        } else {
            // Real email implementation would go here
            // Using JavaMailSender
            log.info("Sending real email to: {}", email);
        }
    }

    public void sendTransferNotification(String email, String type, String amount, String label) {
        if (mockEnabled) {
            log.info("========================================");
            log.info("MOCK EMAIL - Transfer Notification");
            log.info("To: {}", email);
            log.info("Type: {}", type);
            log.info("Amount: {} EUR", amount);
            log.info("Label: {}", label);
            log.info("========================================");
        }
    }
}
