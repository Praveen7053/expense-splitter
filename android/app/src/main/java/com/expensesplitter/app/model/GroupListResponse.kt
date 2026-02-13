package com.expensesplitter.app.model

import java.time.LocalDateTime

data class GroupListResponse(
    val groupId: Long,
    val groupName: String,
    val createdBy: String
)

