package com.expensesplitter.app.model

data class LoginResponse(
    val token: String,
    val userId: Long,
    val name: String,
)