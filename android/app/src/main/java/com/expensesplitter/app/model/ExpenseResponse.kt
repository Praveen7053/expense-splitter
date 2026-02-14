package com.expensesplitter.app.model

data class ExpenseResponse(
    val expenseId: Long,
    val description: String,
    val totalAmount: Double,
    val paidById: Long,
    val paidByName: String,
    val expenseDate: String
)
