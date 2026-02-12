package com.expensesplitter.app.model

data class GroupMemberResponse(
    val id: Long,
    val name: String,
    val email: String,
    val role: String
)