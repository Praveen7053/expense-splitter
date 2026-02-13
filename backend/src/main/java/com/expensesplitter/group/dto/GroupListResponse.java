package com.expensesplitter.group.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GroupListResponse {
    private Long groupId;
    private String groupName;
    private LocalDateTime createdBy;
}
