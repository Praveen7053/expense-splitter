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
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerGroupMembers: RecyclerView
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

        val tvTitle = findViewById<TextView>(R.id.tvGroupTitle)
        tvTitle.text = groupName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAddMember).setOnClickListener {

            val addIntent = Intent(this, AddMemberActivity::class.java)
            addIntent.putExtra("GROUP_ID", groupId)
            addIntent.putExtra("GROUP_NAME", groupName)
            startActivity(addIntent)
        }

        recyclerGroupMembers = findViewById(R.id.recyclerGroupMembers)
        recyclerGroupMembers.layoutManager = LinearLayoutManager(this)

        loadGroupMembers()
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

                        recyclerGroupMembers.adapter =
                            MemberAdapter(members)

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

}
