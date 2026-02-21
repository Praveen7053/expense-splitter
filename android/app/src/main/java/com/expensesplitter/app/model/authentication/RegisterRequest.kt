package com.expensesplitter.app.model.authentication

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
