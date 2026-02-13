package com.expensesplitter.expense.entity;

import com.expensesplitter.common.baseEntity.BaseEntity;
import com.expensesplitter.user.entity.UserExpensesSplitter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "expense_participants")
public class ExpenseParticipant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserExpensesSplitter user;

    @Column(nullable = false)
    private BigDecimal amountOwed;

    @Column(nullable = false)
    private BigDecimal amountPaid;
}
