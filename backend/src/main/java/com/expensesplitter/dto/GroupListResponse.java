package com.expensesplitter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GroupListResponse {
    private Long id;
    private String name;
}
