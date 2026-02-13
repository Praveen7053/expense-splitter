package com.expensesplitter.settlement.entity;

import com.expensesplitter.common.baseEntity.BaseEntity;
import com.expensesplitter.group.entity.GroupEntity;
import com.expensesplitter.user.entity.UserExpensesSplitter;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "settlements")
public class Settlement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private GroupEntity group;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by", nullable = false)
    private UserExpensesSplitter paidBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_to", nullable = false)
    private UserExpensesSplitter paidTo;

    @Column(nullable = false)
    private BigDecimal amount;

    private LocalDate settlementDate;

    private LocalDateTime createdAt;
}
