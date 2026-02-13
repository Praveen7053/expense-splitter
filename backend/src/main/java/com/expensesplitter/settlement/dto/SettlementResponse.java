package com.expensesplitter.settlement.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettlementResponse {

    private Long id;
    private Long paidById;
    private String paidByName;
    private Long paidToId;
    private String paidToName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
