package com.expensesplitter.expense.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceResponse {

    private Long userId;
    private String userName;
    private BigDecimal netBalance;
}
