package com.expensesplitter.controller;

import com.expensesplitter.dto.*;
import com.expensesplitter.dto.apiResponse.ApiResponse;
import com.expensesplitter.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        authService.register(request);

        RegisterResponse response =
                new RegisterResponse("User registered successfully");

        return new ApiResponse<>(
                true,
                "Registration successful",
                response
        );
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(
            @Valid @RequestBody LoginRequest request) {

        String token = authService.login(request);

        LoginResponse response = new LoginResponse(token);

        return new ApiResponse<>(
                true,
                "Login successful",
                response
        );
    }
}
