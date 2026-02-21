package com.expensesplitter.app.ui.features.groups

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.databinding.GroupsActivityCreateGroupBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.groups.CreateGroupRequest
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var binding: GroupsActivityCreateGroupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = GroupsActivityCreateGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.btnCreateGroup.setOnClickListener {

            val name = binding.etGroupName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter group name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val request = CreateGroupRequest(name)

            RetrofitClient.getApiService(this)
                .createGroup(request)
                .enqueue(object : Callback<ApiResponse<Void>> {

                    override fun onResponse(
                        call: Call<ApiResponse<Void>>,
                        response: Response<ApiResponse<Void>>
                    ) {

                        if (response.isSuccessful && response.body()?.success == true) {

                            val message =
                                response.body()?.message ?: "Group created successfully"

                            Toast.makeText(
                                this@CreateGroupActivity,
                                message,
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        } else {
                            Toast.makeText(
                                this@CreateGroupActivity,
                                response.body()?.message ?: "Failed to create group",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponse<Void>>,
                        t: Throwable
                    ) {
                        Toast.makeText(
                            this@CreateGroupActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
