package com.expensesplitter.settlement.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SettlementSummaryResponse {

    private Long fromUserId;
    private String fromUserName;

    private Long toUserId;
    private String toUserName;

    private BigDecimal amount;
}

