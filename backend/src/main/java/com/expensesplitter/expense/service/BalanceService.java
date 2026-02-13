package com.expensesplitter.expense.service;

import com.expensesplitter.expense.entity.ExpenseParticipant;
import com.expensesplitter.settlement.entity.Settlement;
import com.expensesplitter.expense.repository.ExpenseParticipantRepository;
import com.expensesplitter.settlement.repository.SettlementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final ExpenseParticipantRepository participantRepository;
    private final SettlementRepository settlementRepository;

    public BigDecimal calculateUserBalance(Long userId, Long groupId) {

        List<ExpenseParticipant> participants =
                participantRepository.findByUserIdAndExpense_Group_Id(userId, groupId);

        BigDecimal expenseBalance = participants.stream()
                .map(p -> p.getAmountPaid().subtract(p.getAmountOwed()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<Settlement> paid =
                settlementRepository.findByPaidByIdAndGroupId(userId, groupId);

        List<Settlement> received =
                settlementRepository.findByPaidToIdAndGroupId(userId, groupId);

        BigDecimal paidAmount = paid.stream()
                .map(Settlement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal receivedAmount = received.stream()
                .map(Settlement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return expenseBalance
                .subtract(paidAmount)
                .add(receivedAmount);
    }

}

