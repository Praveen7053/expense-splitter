package com.expensesplitter.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.AddMemberRequest
import com.expensesplitter.app.model.GroupMemberResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMemberActivity : AppCompatActivity() {

    private var groupId: Long = -1
    private lateinit var etEmail: EditText
    private lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        val groupName = intent.getStringExtra("GROUP_NAME")

        findViewById<TextView>(R.id.tvGroupName).text = groupName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        etEmail = findViewById(R.id.etEmail)
        btnAdd = findViewById(R.id.btnAdd)

        btnAdd.setOnClickListener {
            val email = etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addMember(email)
        }


        loadMembers()
    }

    private lateinit var recyclerMembers: RecyclerView

    private fun loadMembers() {
        recyclerMembers = findViewById(R.id.recyclerMembers)
        recyclerMembers.layoutManager = LinearLayoutManager(this)

        RetrofitClient.getApiService(this)
            .getGroupMembers(groupId)
            .enqueue(object : Callback<ApiResponse<List<GroupMemberResponse>>> {

                override fun onResponse(
                    call: Call<ApiResponse<List<GroupMemberResponse>>>,
                    response: Response<ApiResponse<List<GroupMemberResponse>>>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val members = response.body()?.data ?: emptyList()

                        recyclerMembers.adapter = MemberAdapter(members)

                    } else {
                        Toast.makeText(
                            this@AddMemberActivity,
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
                        this@AddMemberActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun addMember(email: String) {

        val request = AddMemberRequest(email)

        RetrofitClient.getApiService(this)
            .addMember(groupId, request)
            .enqueue(object : Callback<ApiResponse<Void>> {

                override fun onResponse(
                    call: Call<ApiResponse<Void>>,
                    response: Response<ApiResponse<Void>>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val message = response.body()?.message
                            ?: "Member added successfully"

                        Toast.makeText(
                            this@AddMemberActivity,
                            message,
                            Toast.LENGTH_SHORT
                        ).show()

                        etEmail.text.clear()

                        // Refresh list after successful add
                        loadMembers()

                    } else {

                        Toast.makeText(
                            this@AddMemberActivity,
                            response.body()?.message ?: "Failed to add member",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<Void>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@AddMemberActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}
