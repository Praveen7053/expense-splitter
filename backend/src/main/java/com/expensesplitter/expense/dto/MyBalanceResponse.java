package com.expensesplitter.expense.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MyBalanceResponse {

    private Long userId;
    private String userName;
    private BigDecimal netBalance;
}
