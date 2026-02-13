package com.expensesplitter.settlement.service;

import com.expensesplitter.settlement.entity.Settlement;
import com.expensesplitter.group.repository.GroupRepository;
import com.expensesplitter.settlement.repository.SettlementRepository;
import com.expensesplitter.user.repository.UserExpensesSplitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class SettlementService {

    private final SettlementRepository settlementRepository;
    private final GroupRepository groupRepository;
    private final UserExpensesSplitterRepository userRepository;

    public void settle(Long groupId, Long paidBy, Long paidTo, BigDecimal amount) {

        Settlement settlement = Settlement.builder()
                .group(groupRepository.findById(groupId).orElseThrow())
                .paidBy(userRepository.findById(paidBy).orElseThrow())
                .paidTo(userRepository.findById(paidTo).orElseThrow())
                .amount(amount)
                .settlementDate(LocalDate.now())
                .build();

        settlementRepository.save(settlement);
    }
}

