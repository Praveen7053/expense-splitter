package com.expensesplitter.group.repository;

import com.expensesplitter.group.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByUserId(Long userId);

    Optional<GroupMember> findByGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByGroup_Id(Long groupId);

    List<GroupMember> findByGroupIdAndIsActiveTrue(Long groupId);

    boolean existsByGroupIdAndUserIdAndIsActiveTrue(Long groupId, Long userId);
}
