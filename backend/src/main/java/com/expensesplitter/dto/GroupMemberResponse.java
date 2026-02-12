package com.expensesplitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupMemberResponse {
    private Long id;
    private String name;
    private String email;
    private String role;
}

