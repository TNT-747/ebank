package com.ebank.service;

import com.ebank.dto.ClientDTO;
import com.ebank.dto.CreateClientRequest;
import com.ebank.entity.Client;
import com.ebank.entity.Role;
import com.ebank.entity.User;
import com.ebank.exception.BusinessException;
import com.ebank.repository.ClientRepository;
import com.ebank.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public ClientDTO createClient(CreateClientRequest request) {
        // RG_4: Identity number must be unique
        if (clientRepository.existsByIdentityNumber(request.getIdentityNumber())) {
            throw new BusinessException("Le numéro d'identité existe déjà");
        }

        // RG_6: Email must be unique
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("L'adresse email existe déjà");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("L'adresse email existe déjà");
        }

        // Generate username and password
        String username = generateUsername(request.getFirstName(), request.getLastName());
        String rawPassword = generatePassword();

        // Create user account with encrypted password (RG_1)
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(rawPassword))
                .email(request.getEmail())
                .role(Role.CLIENT)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        // Create client
        Client client = Client.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .identityNumber(request.getIdentityNumber())
                .birthDate(request.getBirthDate())
                .email(request.getEmail())
                .address(request.getAddress())
                .user(user)
                .build();

        client = clientRepository.save(client);

        // RG_7: Send email with credentials
        emailService.sendCredentials(request.getEmail(), username, rawPassword);

        return mapToDTO(client);
    }

    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ClientDTO getClientByIdentityNumber(String identityNumber) {
        Client client = clientRepository.findByIdentityNumber(identityNumber)
                .orElseThrow(() -> new BusinessException("Client non trouvé"));
        return mapToDTO(client);
    }

    private String generateUsername(String firstName, String lastName) {
        String baseUsername = (firstName.charAt(0) + lastName).toLowerCase()
                .replaceAll("[^a-z0-9]", "");
        String username = baseUsername;
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        return username;
    }

    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < 10; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    private ClientDTO mapToDTO(Client client) {
        return ClientDTO.builder()
                .id(client.getId())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .identityNumber(client.getIdentityNumber())
                .birthDate(client.getBirthDate())
                .email(client.getEmail())
                .address(client.getAddress())
                .username(client.getUser() != null ? client.getUser().getUsername() : null)
                .build();
    }
}
