package com.expensesplitter.app.model

data class CreateExpenseRequest(
    val description: String,
    val totalAmount: Double,
    val paidBy: Long,
    val participantIds: List<Long>
)
