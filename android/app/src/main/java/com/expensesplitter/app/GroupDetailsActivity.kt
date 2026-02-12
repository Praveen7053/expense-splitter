package com.expensesplitter.app

import android.content.Intent
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
class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var recyclerGroupMembers: RecyclerView
    private var groupId: Long = -1

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
            .enqueue(object : Callback<List<GroupMemberResponse>> {

                override fun onResponse(
                    call: Call<List<GroupMemberResponse>>,
                    response: Response<List<GroupMemberResponse>>
                ) {
                    if (response.isSuccessful) {
                        recyclerGroupMembers.adapter =
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
