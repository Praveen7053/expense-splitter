package com.expensesplitter.app.network

import com.expensesplitter.app.model.CreateGroupRequest
import com.expensesplitter.app.model.LoginRequest
import com.expensesplitter.app.model.LoginResponse
import com.expensesplitter.app.model.MessageResponse
import com.expensesplitter.app.model.RegisterRequest
import com.expensesplitter.app.model.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/groups")
    fun createGroup(@Body request: CreateGroupRequest): Call<MessageResponse>

}
