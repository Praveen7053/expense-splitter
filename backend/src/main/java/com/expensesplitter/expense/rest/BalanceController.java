package com.expensesplitter.expense.rest;

import com.expensesplitter.common.response.ApiResponse;
import com.expensesplitter.expense.dto.BalanceResponse;
import com.expensesplitter.expense.dto.MyBalanceResponse;
import com.expensesplitter.expense.service.BalanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/balances")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @GetMapping
    public ApiResponse<List<BalanceResponse>> getGroupBalances(
            @PathVariable Long groupId,
            Authentication authentication) {

        return new ApiResponse<>(
                true,
                "Balances fetched successfully",
                balanceService.getGroupBalances(
                        groupId,
                        authentication.getName())
        );
    }

    @GetMapping("/my-balance")
    public ApiResponse<MyBalanceResponse> getMyBalance(
            @PathVariable Long groupId,
            Authentication authentication) {

        return new ApiResponse<>(
                true,
                "Balance fetched successfully",
                balanceService.getMyBalance(groupId, authentication.getName())
        );
    }

}
