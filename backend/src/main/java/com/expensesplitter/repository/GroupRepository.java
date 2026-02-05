package com.expensesplitter.repository;

import com.expensesplitter.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    List<GroupEntity> findByOwnerId(Long ownerId);
}
