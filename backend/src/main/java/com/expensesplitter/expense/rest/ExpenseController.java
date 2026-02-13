package com.expensesplitter.expense.rest;

import com.expensesplitter.common.response.ApiResponse;
import com.expensesplitter.expense.dto.CreateExpenseRequest;
import com.expensesplitter.expense.dto.ExpenseResponse;
import com.expensesplitter.expense.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    /**
     * 1️⃣ Create Expense
     */
    @PostMapping
    public ApiResponse<String> createExpense(
            @PathVariable Long groupId,
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {

        request.setGroupId(groupId);

        expenseService.createExpense(request, authentication.getName());

        return new ApiResponse<>(true, "Expense created successfully", null);
    }

    /**
     * 2️⃣ Get All Expenses of a Group
     */
    @GetMapping
    public ApiResponse<List<ExpenseResponse>> getExpensesByGroup(
            @PathVariable Long groupId,
            Authentication authentication) {

        return new ApiResponse<>(
                true,
                "Expenses fetched successfully",
                expenseService.getExpensesByGroup(groupId, authentication.getName())
        );
    }

    /**
     * 3️⃣ Get Single Expense
     */
    @GetMapping("/{expenseId}")
    public ApiResponse<ExpenseResponse> getExpenseById(
            @PathVariable Long groupId,
            @PathVariable Long expenseId,
            Authentication authentication) {

        return new ApiResponse<>(
                true,
                "Expense fetched successfully",
                expenseService.getExpenseById(groupId, expenseId, authentication.getName())
        );
    }

    /**
     * 4️⃣ Update Expense
     */
    @PutMapping("/{expenseId}")
    public ApiResponse<String> updateExpense(
            @PathVariable Long groupId,
            @PathVariable Long expenseId,
            @Valid @RequestBody CreateExpenseRequest request,
            Authentication authentication) {

        request.setGroupId(groupId);

        expenseService.editExpense(groupId, expenseId, request, authentication.getName());

        return new ApiResponse<>(true, "Expense updated successfully", null);
    }

    /**
     * 5️⃣ Delete Expense
     */
    @DeleteMapping("/{expenseId}")
    public ApiResponse<String> deleteExpense(
            @PathVariable Long groupId,
            @PathVariable Long expenseId,
            Authentication authentication) {

        expenseService.deleteExpense(groupId, expenseId, authentication.getName());

        return new ApiResponse<>(true, "Expense deleted successfully", null);
    }
}

