package com.expensesplitter.settlement.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSettlementRequest {

    private Long paidToUserId;
    private BigDecimal amount;
}