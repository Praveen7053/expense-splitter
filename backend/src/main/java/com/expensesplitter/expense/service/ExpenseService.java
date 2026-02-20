package com.expensesplitter.expense.service;

import com.expensesplitter.expense.dto.CreateExpenseRequest;
import com.expensesplitter.expense.dto.ExpenseParticipantResponse;
import com.expensesplitter.expense.dto.ExpenseResponse;
import com.expensesplitter.expense.entity.Expense;
import com.expensesplitter.expense.entity.ExpenseParticipant;
import com.expensesplitter.expense.helper.ExpenseSplitHelper;
import com.expensesplitter.expense.helper.ExpenseValidator;
import com.expensesplitter.expense.repository.ExpenseParticipantRepository;
import com.expensesplitter.expense.repository.ExpenseRepository;
import com.expensesplitter.group.entity.GroupEntity;
import com.expensesplitter.group.repository.GroupMemberRepository;
import com.expensesplitter.group.repository.GroupRepository;
import com.expensesplitter.user.entity.UserExpensesSplitter;
import com.expensesplitter.user.repository.UserExpensesSplitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final ExpenseParticipantRepository participantRepository;
    private final GroupRepository groupRepository;
    private final UserExpensesSplitterRepository userRepository;
    private final ExpenseValidator expenseValidator;
    private final GroupMemberRepository groupMemberRepository;

    @Transactional
    public void createExpense(CreateExpenseRequest request, String email) {

        validateGroupAccess(request.getGroupId(), email);

        expenseValidator.validate(request);

        GroupEntity group = groupRepository.findById(request.getGroupId()).orElseThrow();

        UserExpensesSplitter paidBy = userRepository.findById(request.getPaidBy()).orElseThrow();

        Expense expense = Expense.builder()
                .group(group)
                .description(request.getDescription())
                .totalAmount(request.getTotalAmount())
                .paidBy(paidBy)
                .expenseDate(LocalDate.now())
                .build();

        expenseRepository.save(expense);

        int count = request.getParticipantIds().size();

        BigDecimal splitAmount =
                ExpenseSplitHelper.calculateEqualSplit(request.getTotalAmount(), count);

        for (Long userId : request.getParticipantIds()) {

            UserExpensesSplitter user = userRepository.findById(userId).orElseThrow();

            BigDecimal paidAmount = userId.equals(paidBy.getId())
                    ? request.getTotalAmount()
                    : BigDecimal.ZERO;

            ExpenseParticipant participant = ExpenseParticipant.builder()
                    .expense(expense)
                    .user(user)
                    .amountOwed(splitAmount)
                    .amountPaid(paidAmount)
                    .build();

            participantRepository.save(participant);
        }
    }

    @Transactional(readOnly = true)
    public List<ExpenseResponse> getExpensesByGroup(Long groupId, String email) {

        validateGroupAccess(groupId, email);

        List<Expense> expenses = expenseRepository.findByGroupId(groupId);

        return expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExpenseResponse getExpenseById(Long groupId, Long expenseId, String email) {

        validateGroupAccess(groupId, email);  // 🔥 ADD THIS

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Expense does not belong to this group");
        }

        return mapToExpenseResponse(expense);
    }

    private ExpenseResponse mapToExpenseResponse(Expense expense) {

        List<ExpenseParticipantResponse> participants =
                expense.getParticipants().stream()
                        .map(p -> {
                            BigDecimal netBalance =
                                    p.getAmountPaid().subtract(p.getAmountOwed());

                            return ExpenseParticipantResponse.builder()
                                    .userId(p.getUser().getId())
                                    .userName(p.getUser().getName())
                                    .amountOwed(p.getAmountOwed())
                                    .amountPaid(p.getAmountPaid())
                                    .netBalance(netBalance)
                                    .build();
                        })
                        .collect(Collectors.toList());

        return ExpenseResponse.builder()
                .expenseId(expense.getId())
                .description(expense.getDescription())
                .totalAmount(expense.getTotalAmount())
                .paidById(expense.getPaidBy().getId())
                .paidByName(expense.getPaidBy().getName())
                .expenseDate(expense.getExpenseDate())
                .participants(participants)
                .build();
    }

    @Transactional
    public void editExpense(Long groupId, Long expenseId, CreateExpenseRequest request, String email) {

        validateGroupAccess(groupId, email);

        expenseValidator.validate(request);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        // Validate expense belongs to group
        if (!expense.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Expense does not belong to this group");
        }

        // Update header
        expense.setDescription(request.getDescription());
        expense.setTotalAmount(request.getTotalAmount());

        UserExpensesSplitter paidBy =
                userRepository.findById(request.getPaidBy()).orElseThrow();

        expense.setPaidBy(paidBy);

        // Remove old participants
        participantRepository.deleteByExpenseId(expenseId);

        // Recreate participants
        int count = request.getParticipantIds().size();

        BigDecimal splitAmount =
                ExpenseSplitHelper.calculateEqualSplit(request.getTotalAmount(), count);

        for (Long userId : request.getParticipantIds()) {

            UserExpensesSplitter user =
                    userRepository.findById(userId).orElseThrow();

            BigDecimal paidAmount = userId.equals(paidBy.getId())
                    ? request.getTotalAmount()
                    : BigDecimal.ZERO;

            ExpenseParticipant participant = ExpenseParticipant.builder()
                    .expense(expense)
                    .user(user)
                    .amountOwed(splitAmount)
                    .amountPaid(paidAmount)
                    .build();

            participantRepository.save(participant);
        }
    }

    @Transactional
    public void deleteExpense(Long groupId, Long expenseId, String email) {

        validateGroupAccess(groupId, email);

        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getGroup().getId().equals(groupId)) {
            throw new RuntimeException("Expense does not belong to this group");
        }

        expenseRepository.delete(expense);
    }

    private void validateGroupAccess(Long groupId, String email) {

        UserExpensesSplitter user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean isMember = groupMemberRepository
                .existsByGroupIdAndUserIdAndIsActiveTrue(groupId, user.getId());

        if (!isMember) {
            throw new RuntimeException("You are not a member of this group");
        }
    }


}
