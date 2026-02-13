package com.expensesplitter.expense.service;

import com.expensesplitter.expense.dto.BalanceResponse;
import com.expensesplitter.expense.dto.MyBalanceResponse;
import com.expensesplitter.expense.entity.ExpenseParticipant;
import com.expensesplitter.expense.repository.ExpenseParticipantRepository;
import com.expensesplitter.group.repository.GroupMemberRepository;
import com.expensesplitter.settlement.entity.Settlement;
import com.expensesplitter.settlement.repository.SettlementRepository;
import com.expensesplitter.user.entity.UserExpensesSplitter;
import com.expensesplitter.user.repository.UserExpensesSplitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final ExpenseParticipantRepository participantRepository;
    private final SettlementRepository settlementRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserExpensesSplitterRepository userRepository;

    @Transactional(readOnly = true)
    public List<BalanceResponse> getGroupBalances(Long groupId, String email) {

        // Validate logged-in user membership
        UserExpensesSplitter loggedInUser =
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isMember = groupMemberRepository
                .existsByGroupIdAndUserIdAndIsActiveTrue(groupId, loggedInUser.getId());

        if (!isMember) {
            throw new RuntimeException("You are not a member of this group");
        }

        // Get all active members
        List<UserExpensesSplitter> members =
                groupMemberRepository.findByGroupIdAndIsActiveTrue(groupId)
                        .stream()
                        .map(gm -> gm.getUser())
                        .toList();

        // Calculate balance per member
        return members.stream()
                .map(user -> BalanceResponse.builder()
                        .userId(user.getId())
                        .userName(user.getName())
                        .netBalance(
                                calculateUserBalance(user.getId(), groupId)
                        )
                        .build())
                .toList();
    }

    private BigDecimal calculateUserBalance(Long userId, Long groupId) {

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

    public MyBalanceResponse getMyBalance(Long groupId, String email) {

        UserExpensesSplitter user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isMember = groupMemberRepository
                .existsByGroupIdAndUserIdAndIsActiveTrue(groupId, user.getId());

        if (!isMember) {
            throw new RuntimeException("You are not a member of this group");
        }

        BigDecimal balance = calculateUserBalance(user.getId(), groupId);

        return MyBalanceResponse.builder()
                .userId(user.getId())
                .userName(user.getName())
                .netBalance(balance)
                .build();
    }

}
