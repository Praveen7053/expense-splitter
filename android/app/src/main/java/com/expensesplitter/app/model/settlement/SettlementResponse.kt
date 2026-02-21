package com.expensesplitter.app.model.settlement

import java.math.BigDecimal

data class SettlementResponse(
    val id: Long,
    val paidById: Long,
    val paidByName: String,
    val paidToId: Long,
    val paidToName: String,
    val amount: BigDecimal,
    val createdAt: String
)
