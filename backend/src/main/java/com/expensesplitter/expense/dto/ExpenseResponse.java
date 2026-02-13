package com.expensesplitter.expense.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private Long expenseId;

    private String description;

    private BigDecimal totalAmount;

    private Long paidById;
    private String paidByName;

    private LocalDate expenseDate;

    private List<ExpenseParticipantResponse> participants;
}
