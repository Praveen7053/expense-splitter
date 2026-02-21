package com.expensesplitter.app.network

import com.expensesplitter.app.model.authentication.LoginRequest
import com.expensesplitter.app.model.authentication.LoginResponse
import com.expensesplitter.app.model.authentication.RegisterRequest
import com.expensesplitter.app.model.authentication.RegisterResponse
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.expenses.CreateExpenseRequest
import com.expensesplitter.app.model.expenses.ExpenseResponse
import com.expensesplitter.app.model.groups.AddMemberRequest
import com.expensesplitter.app.model.expenses.BalanceResponse
import com.expensesplitter.app.model.groups.CreateGroupRequest
import com.expensesplitter.app.model.groups.GroupListResponse
import com.expensesplitter.app.model.groups.GroupMemberResponse
import com.expensesplitter.app.model.expenses.MyBalanceResponse
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

    @GET("api/groups/{groupId}/balances")
    fun getGroupBalances(
        @Path("groupId") groupId: Long
    ): Call<ApiResponse<List<BalanceResponse>>>

    @GET("api/groups/{groupId}/expenses")
    fun getExpenses(
        @Path("groupId") groupId: Long
    ): Call<ApiResponse<List<ExpenseResponse>>>

    @POST("api/groups/{groupId}/expenses")
    fun createExpense(
        @Path("groupId") groupId: Long,
        @Body request: CreateExpenseRequest
    ): Call<ApiResponse<String>>

    @GET("api/groups/{groupId}/expenses/{expenseId}")
    fun getExpenseById(
        @Path("groupId") groupId: Long,
        @Path("expenseId") expenseId: Long
    ): Call<ApiResponse<ExpenseResponse>>

}
