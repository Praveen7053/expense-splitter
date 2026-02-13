package com.expensesplitter.user.repository;

import com.expensesplitter.user.entity.UserExpensesSplitter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserExpensesSplitterRepository extends JpaRepository<UserExpensesSplitter, Long> {
    Optional<UserExpensesSplitter> findByEmail(String email);
    boolean existsByEmail(String email);
}
