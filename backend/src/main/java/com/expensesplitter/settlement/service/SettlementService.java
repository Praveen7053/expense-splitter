package com.expensesplitter.settlement.service;

import com.expensesplitter.common.exception.BadRequestException;
import com.expensesplitter.group.entity.GroupEntity;
import com.expensesplitter.group.repository.GroupMemberRepository;
import com.expensesplitter.group.repository.GroupRepository;
import com.expensesplitter.settlement.dto.CreateSettlementRequest;
import com.expensesplitter.settlement.dto.SettlementResponse;
import com.expensesplitter.settlement.entity.Settlement;
import com.expensesplitter.settlement.repository.SettlementRepository;
import com.expensesplitter.user.entity.UserExpensesSplitter;
import com.expensesplitter.user.repository.UserExpensesSplitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserExpensesSplitterRepository userRepository;

    @Transactional
    public void createSettlement(Long groupId,
                                 CreateSettlementRequest request,
                                 String email) {

        if (request.getAmount() == null ||
                request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Settlement amount must be positive");
        }

        UserExpensesSplitter paidBy =
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        if (paidBy.getId().equals(request.getPaidToUserId())) {
            throw new BadRequestException("You cannot settle with yourself");
        }

        validateMembership(groupId, paidBy.getId());
        validateMembership(groupId, request.getPaidToUserId());

        GroupEntity group =
                groupRepository.findById(groupId)
                        .orElseThrow(() -> new RuntimeException("Group not found"));

        UserExpensesSplitter paidTo =
                userRepository.findById(request.getPaidToUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));

        Settlement settlement = Settlement.builder()
                .group(group)
                .paidBy(paidBy)
                .paidTo(paidTo)
                .amount(request.getAmount())
                .build();

        settlementRepository.save(settlement);
    }

    @Transactional(readOnly = true)
    public List<SettlementResponse> getSettlements(Long groupId, String email) {

        UserExpensesSplitter user =
                userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("User not found"));

        validateMembership(groupId, user.getId());

        return settlementRepository.findByGroupId(groupId)
                .stream()
                .map(s -> SettlementResponse.builder()
                        .id(s.getId())
                        .paidById(s.getPaidBy().getId())
                        .paidByName(s.getPaidBy().getName())
                        .paidToId(s.getPaidTo().getId())
                        .paidToName(s.getPaidTo().getName())
                        .amount(s.getAmount())
                        .createdAt(s.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private void validateMembership(Long groupId, Long userId) {

        boolean isMember = groupMemberRepository
                .existsByGroupIdAndUserIdAndIsActiveTrue(groupId, userId);

        if (!isMember) {
            throw new RuntimeException("User not member of this group");
        }
    }
}
