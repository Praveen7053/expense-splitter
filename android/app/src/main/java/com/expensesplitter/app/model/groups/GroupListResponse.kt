package com.expensesplitter.app.model.groups

import java.time.LocalDateTime

data class GroupListResponse(
    val groupId: Long,
    val groupName: String,
    val createdBy: String
)
