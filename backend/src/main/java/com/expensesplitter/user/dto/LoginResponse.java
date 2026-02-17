package com.expensesplitter.user.dto;

public class LoginResponse {
    private Long userId;
    private String name;
    private String token;

    public LoginResponse(Long userId, String name, String token) {
        this.userId = userId;
        this.name = name;
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getToken() {
        return token;
    }
}
