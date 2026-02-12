package com.expensesplitter.app

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.GroupMemberResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMemberActivity : AppCompatActivity() {

    private var groupId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        val groupName = intent.getStringExtra("GROUP_NAME")

        findViewById<TextView>(R.id.tvGroupName).text = groupName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        loadMembers()
    }

    private lateinit var recyclerMembers: RecyclerView

    private fun loadMembers() {
        recyclerMembers = findViewById(R.id.recyclerMembers)
        recyclerMembers.layoutManager = LinearLayoutManager(this)

        RetrofitClient.getApiService(this)
            .getGroupMembers(groupId)
            .enqueue(object : Callback<List<GroupMemberResponse>> {

                override fun onResponse(
                    call: Call<List<GroupMemberResponse>>,
                    response: Response<List<GroupMemberResponse>>
                ) {
                    if (response.isSuccessful) {
                        recyclerMembers.adapter =
                            MemberAdapter(response.body() ?: emptyList())
                    }
                }

                override fun onFailure(
                    call: Call<List<GroupMemberResponse>>,
                    t: Throwable
                ) {
                }
            })
    }

}
