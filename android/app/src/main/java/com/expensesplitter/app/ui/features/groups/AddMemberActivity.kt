package com.expensesplitter.app.ui.features.groups

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensesplitter.app.databinding.GroupsActivityAddMemberBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.groups.AddMemberRequest
import com.expensesplitter.app.model.groups.GroupMemberResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMemberActivity : AppCompatActivity() {

    private var groupId: Long = -1
    private lateinit var binding: GroupsActivityAddMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupsActivityAddMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        val groupName = intent.getStringExtra("GROUP_NAME")

        binding.tvGroupName.text = groupName

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAdd.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Enter email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            addMember(email)
        }


        loadMembers()
    }

    private fun loadMembers() {
        binding.recyclerMembers.layoutManager = LinearLayoutManager(this)

        RetrofitClient.getApiService(this)
            .getGroupMembers(groupId)
            .enqueue(object : Callback<ApiResponse<List<GroupMemberResponse>>> {

                override fun onResponse(
                    call: Call<ApiResponse<List<GroupMemberResponse>>>,
                    response: Response<ApiResponse<List<GroupMemberResponse>>>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val members = response.body()?.data ?: emptyList()

                        binding.recyclerMembers.adapter = MemberAdapter(members)

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

                        binding.etEmail.text.clear()

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
