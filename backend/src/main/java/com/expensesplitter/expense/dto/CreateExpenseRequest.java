package com.expensesplitter.expense.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpenseRequest {

    private Long groupId;

    private String description;

    private BigDecimal totalAmount;

    private Long paidBy;   // user id

    // Selected members who will split this expense
    private List<Long> participantIds;
}
