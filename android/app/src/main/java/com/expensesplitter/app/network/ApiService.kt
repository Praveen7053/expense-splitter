package com.expensesplitter.app.network

import com.expensesplitter.app.model.LoginRequest
import com.expensesplitter.app.model.LoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>
}
