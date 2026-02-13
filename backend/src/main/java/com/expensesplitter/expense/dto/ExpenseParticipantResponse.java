package com.expensesplitter.expense.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseParticipantResponse {

    private Long userId;
    private String userName;

    private BigDecimal amountOwed;
    private BigDecimal amountPaid;
    private BigDecimal netBalance;
}
