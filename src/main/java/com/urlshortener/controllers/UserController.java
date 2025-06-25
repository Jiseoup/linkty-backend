package com.urlshortener.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.urlshortener.services.UserService;
import com.urlshortener.dto.request.RegisterRequest;
import com.urlshortener.dto.response.RegisterResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    // User register.
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        RegisterResponse response = userService.createAccount(request);
        return ResponseEntity.ok(response);
    }
}
