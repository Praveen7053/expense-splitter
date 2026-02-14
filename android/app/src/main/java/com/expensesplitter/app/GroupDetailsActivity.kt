package com.expensesplitter.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.GroupMemberResponse
import com.expensesplitter.app.model.MyBalanceResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerMembers: RecyclerView
    private lateinit var tvMemberCount: TextView

    private lateinit var tvBalanceTitle: TextView

    private lateinit var tvTotalGroupBalance: TextView

    private var groupId: Long = -1

    override fun onResume() {
        super.onResume()
        loadGroupMembers()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        val groupName = intent.getStringExtra("GROUP_NAME")

        findViewById<TextView>(R.id.tvGroupTitle).text = groupName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAddMember).setOnClickListener {
            val addIntent = Intent(this, AddMemberActivity::class.java)
            addIntent.putExtra("GROUP_ID", groupId)
            addIntent.putExtra("GROUP_NAME", groupName)
            startActivity(addIntent)
        }

        tvMemberCount = findViewById(R.id.tvMemberCount)

        recyclerMembers = findViewById(R.id.recyclerMembersHorizontal)
        recyclerMembers.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        loadGroupMembers()

        tvBalanceTitle = findViewById(R.id.tvBalanceTitle)
        tvTotalGroupBalance = findViewById(R.id.tvTotalGroupBalance)

        if (groupId != -1L) {
            loadMyBalance(groupId)
        }
    }

    private fun loadGroupMembers() {

        RetrofitClient.getApiService(this)
            .getGroupMembers(groupId)
            .enqueue(object : Callback<ApiResponse<List<GroupMemberResponse>>> {

                override fun onResponse(
                    call: Call<ApiResponse<List<GroupMemberResponse>>>,
                    response: Response<ApiResponse<List<GroupMemberResponse>>>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val members = response.body()?.data ?: emptyList()

                        recyclerMembers.adapter = MemberHorizontalAdapter(members)

                        tvMemberCount.text = "Members (${members.size})"

                    } else {

                        Toast.makeText(
                            this@GroupDetailsActivity,
                            response.body()?.message ?: "Failed to load members",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<List<GroupMemberResponse>>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@GroupDetailsActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun loadMyBalance(groupId: Long) {

        RetrofitClient.getApiService(this).getMyBalance(groupId)
            .enqueue(object : Callback<ApiResponse<MyBalanceResponse>> {

                override fun onResponse(
                    call: Call<ApiResponse<MyBalanceResponse>>,
                    response: Response<ApiResponse<MyBalanceResponse>>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {

                        val balance = response.body()?.data?.netBalance

                        if (balance != null) {
                            updateBalanceUI(balance)
                        } else {
                            showError("Balance data missing")
                        }
                    } else {
                        showError("Failed to load balance")
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<MyBalanceResponse>>,
                    t: Throwable
                ) {
                    showError(t.message ?: "Something went wrong")
                }
            })
    }

    private fun updateBalanceUI(balance: Double) {

        when {
            balance < 0 -> {
                tvBalanceTitle.text = "You owe ₹${kotlin.math.abs(balance)}"
            }
            balance > 0 -> {
                tvBalanceTitle.text = "You get ₹$balance"
            }
            else -> {
                tvBalanceTitle.text = "All settled 🎉"
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}
