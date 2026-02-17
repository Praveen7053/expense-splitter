package com.expensesplitter.user.rest;

import com.expensesplitter.common.response.ApiResponse;
import com.expensesplitter.user.service.AuthService;
import com.expensesplitter.user.dto.LoginRequest;
import com.expensesplitter.user.dto.LoginResponse;
import com.expensesplitter.user.dto.RegisterRequest;
import com.expensesplitter.user.dto.RegisterResponse;
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
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        LoginResponse response = authService.login(request);

        return new ApiResponse<>(
                true,
                "Login successful",
                response
        );
    }
}
