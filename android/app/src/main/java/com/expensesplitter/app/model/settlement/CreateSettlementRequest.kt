package com.expensesplitter.app.model.settlement

import java.math.BigDecimal

data class CreateSettlementRequest(
    val paidToUserId: Long,
    val amount: BigDecimal
)
