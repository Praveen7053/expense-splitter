package com.expensesplitter.app.model.expenses

data class MyBalanceResponse(
    val userId: Long,
    val userName: String,
    val netBalance: Double
)
