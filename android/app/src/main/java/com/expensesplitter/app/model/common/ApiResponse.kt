package com.expensesplitter.app.model.common

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T?
)
