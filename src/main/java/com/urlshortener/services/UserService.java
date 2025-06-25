package com.urlshortener.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.urlshortener.entities.User;
import com.urlshortener.repositories.UserRepository;
import com.urlshortener.dto.request.RegisterRequest;
import com.urlshortener.dto.response.RegisterResponse;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Creates a new user account.
    public RegisterResponse createAccount(RegisterRequest request) {
        // Check if the requested email already exists.
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already in use.");
        }

        // Build and save the User entity.
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        return new RegisterResponse(user.getEmail(), user.getJoinDate());
    }
}
