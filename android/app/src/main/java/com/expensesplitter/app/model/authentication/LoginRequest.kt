package com.expensesplitter.app.model.authentication

data class LoginRequest(
    val email: String,
    val password: String
)
