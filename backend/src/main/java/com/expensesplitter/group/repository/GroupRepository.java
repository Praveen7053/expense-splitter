package com.expensesplitter.group.repository;

import com.expensesplitter.group.entity.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    List<GroupEntity> findByOwnerId(Long ownerId);
}
