package com.expensesplitter.app.model.groups

data class GroupMemberResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)