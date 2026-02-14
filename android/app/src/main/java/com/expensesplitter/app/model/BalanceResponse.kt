package com.expensesplitter.app.model

data class BalanceResponse(
    val userId: Long,
    val userName: String,
    val netBalance: Double
)
