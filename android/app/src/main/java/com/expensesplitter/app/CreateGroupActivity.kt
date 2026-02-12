package com.expensesplitter.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.model.CreateGroupRequest
import com.expensesplitter.app.model.MessageResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        val imgBack = findViewById<ImageView>(R.id.imgBack)
        val etGroupName = findViewById<EditText>(R.id.etGroupName)
        val btnCreate = findViewById<Button>(R.id.btnCreateGroup)

        imgBack.setOnClickListener {
            finish()
        }

        btnCreate.setOnClickListener {

            val name = etGroupName.text.toString().trim()

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
