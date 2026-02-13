package com.expensesplitter.settlement.rest;

import com.expensesplitter.common.response.ApiResponse;
import com.expensesplitter.settlement.dto.CreateSettlementRequest;
import com.expensesplitter.settlement.dto.SettlementResponse;
import com.expensesplitter.settlement.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups/{groupId}/settlements")
@RequiredArgsConstructor
public class SettlementController {

    private final SettlementService settlementService;

    @PostMapping
    public ApiResponse<String> createSettlement(
            @PathVariable Long groupId,
            @RequestBody CreateSettlementRequest request,
            Authentication authentication) {

        settlementService.createSettlement(
                groupId,
                request,
                authentication.getName());

        return new ApiResponse<>(true,
                "Settlement recorded successfully",
                null);
    }

    @GetMapping
    public ApiResponse<List<SettlementResponse>> getSettlements(
            @PathVariable Long groupId,
            Authentication authentication) {

        return new ApiResponse<>(
                true,
                "Settlements fetched successfully",
                settlementService.getSettlements(
                        groupId,
                        authentication.getName())
        );
    }
}
