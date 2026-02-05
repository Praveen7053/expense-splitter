package com.expensesplitter.controller;

import com.expensesplitter.dto.LoginRequest;
import com.expensesplitter.dto.LoginResponse;
import com.expensesplitter.dto.RegisterRequest;
import com.expensesplitter.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {

        String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }


}
