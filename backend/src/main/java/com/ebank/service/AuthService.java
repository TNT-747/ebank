package com.ebank.service;

import com.ebank.dto.ChangePasswordRequest;
import com.ebank.dto.LoginRequest;
import com.ebank.dto.LoginResponse;
import com.ebank.entity.User;
import com.ebank.exception.BusinessException;
import com.ebank.repository.UserRepository;
import com.ebank.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenProvider.generateToken(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BadCredentialsException("User not found"));

            return LoginResponse.builder()
                    .token(token)
                    .username(user.getUsername())
                    .role(user.getRole().name())
                    .email(user.getEmail())
                    .expiresIn(jwtTokenProvider.getExpirationTime())
                    .build();
        } catch (Exception e) {
            throw new BadCredentialsException("Login ou mot de passe erronés");
        }
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException("Le mot de passe actuel est incorrect");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException("Les mots de passe ne correspondent pas");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
