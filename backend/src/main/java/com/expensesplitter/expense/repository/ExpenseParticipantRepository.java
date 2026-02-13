package com.expensesplitter.expense.repository;

import com.expensesplitter.expense.entity.ExpenseParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseParticipantRepository extends JpaRepository<ExpenseParticipant, Long> {

    List<ExpenseParticipant> findByExpenseId(Long expenseId);

    List<ExpenseParticipant> findByUserIdAndExpense_Group_Id(Long userId, Long groupId);

    void deleteByExpenseId(Long expenseId);
}

