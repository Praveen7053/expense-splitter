package com.expensesplitter.expense.helper;

import com.expensesplitter.common.exception.BadRequestException;
import com.expensesplitter.expense.dto.CreateExpenseRequest;
import com.expensesplitter.group.repository.GroupMemberRepository;
import com.expensesplitter.group.repository.GroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ExpenseValidator {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;

    public void validate(CreateExpenseRequest request) {

        if (request.getParticipantIds() == null || request.getParticipantIds().isEmpty()) {
            throw new BadRequestException("Participants list cannot be empty");
        }

        if (request.getTotalAmount() == null ||
                request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Total amount must be greater than zero");
        }

        groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new BadRequestException("Group not found"));

        Set<Long> groupMemberIds = groupMemberRepository
                .findByGroupIdAndIsActiveTrue(request.getGroupId())
                .stream()
                .map(gm -> gm.getUser().getId())
                .collect(Collectors.toSet());

        if (!groupMemberIds.contains(request.getPaidBy())) {
            throw new BadRequestException("PaidBy user is not part of this group");
        }

        for (Long participantId : request.getParticipantIds()) {
            if (!groupMemberIds.contains(participantId)) {
                throw new BadRequestException("User " + participantId + " not part of group");
            }
        }

        if (!request.getParticipantIds().contains(request.getPaidBy())) {
            throw new BadRequestException("PaidBy must be included in participants");
        }
    }

}

