package com.expensesplitter.app.network

import com.expensesplitter.app.model.AddMemberRequest
import com.expensesplitter.app.model.CreateGroupRequest
import com.expensesplitter.app.model.GroupListResponse
import com.expensesplitter.app.model.GroupMemberResponse
import com.expensesplitter.app.model.LoginRequest
import com.expensesplitter.app.model.LoginResponse
import com.expensesplitter.app.model.MyBalanceResponse
import com.expensesplitter.app.model.RegisterRequest
import com.expensesplitter.app.model.RegisterResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    // ---------------- AUTH ----------------

    @POST("api/auth/login")
    fun login(
        @Body request: LoginRequest
    ): Call<ApiResponse<LoginResponse>>

    @POST("api/auth/register")
    fun register(
        @Body request: RegisterRequest
    ): Call<ApiResponse<RegisterResponse>>


    // ---------------- GROUP ----------------

    @POST("api/groups")
    fun createGroup(
        @Body request: CreateGroupRequest
    ): Call<ApiResponse<Void>>

    @GET("api/groups")
    fun getGroupList(): Call<ApiResponse<List<GroupListResponse>>>

    @GET("api/groups/{groupId}/members")
    fun getGroupMembers(
        @Path("groupId") groupId: Long
    ): Call<ApiResponse<List<GroupMemberResponse>>>

    @POST("api/groups/{groupId}/members")
    fun addMember(
        @Path("groupId") groupId: Long,
        @Body request: AddMemberRequest
    ): Call<ApiResponse<Void>>

    @GET("api/groups/{groupId}/balances/my-balance")
    fun getMyBalance(
        @Path("groupId") groupId: Long
    ): Call<ApiResponse<MyBalanceResponse>>


}
