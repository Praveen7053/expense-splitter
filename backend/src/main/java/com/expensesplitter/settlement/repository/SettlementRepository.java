package com.expensesplitter.settlement.repository;

import com.expensesplitter.settlement.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    List<Settlement> findByGroupId(Long groupId);

    List<Settlement> findByPaidByIdAndGroupId(Long userId, Long groupId);

    List<Settlement> findByPaidToIdAndGroupId(Long userId, Long groupId);
}

