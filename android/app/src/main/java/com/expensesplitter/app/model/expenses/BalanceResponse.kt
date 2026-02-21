package com.expensesplitter.app.model.expenses

data class BalanceResponse(
    val userId: Long,
    val userName: String,
    val netBalance: Double
)
