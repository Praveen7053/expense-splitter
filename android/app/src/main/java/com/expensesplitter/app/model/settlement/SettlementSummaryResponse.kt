package com.expensesplitter.app.model.settlement

import java.math.BigDecimal

data class SettlementSummaryResponse(
    val fromUserId: Long,
    val fromUserName: String,
    val toUserId: Long,
    val toUserName: String,
    val amount: BigDecimal
)
