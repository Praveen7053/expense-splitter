package com.expensesplitter.app.model

import java.math.BigDecimal

data class ExpenseParticipantResponse(
    val userId: Long,
    val userName: String,
    val amountOwed: BigDecimal,
    val amountPaid: BigDecimal,
    val netBalance: BigDecimal
)
