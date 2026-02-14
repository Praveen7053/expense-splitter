package com.expensesplitter.app.model

data class MyBalanceResponse(
    val userId: Long,
    val userName: String,
    val netBalance: Double
)
