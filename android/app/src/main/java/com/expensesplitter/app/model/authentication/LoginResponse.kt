package com.expensesplitter.app.model.authentication

data class LoginResponse(
    val token: String,
    val userId: Long,
    val name: String,
)
