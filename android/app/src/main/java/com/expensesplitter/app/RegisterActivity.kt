package com.expensesplitter.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.model.RegisterRequest
import com.expensesplitter.app.model.RegisterResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)

        btnRegister.setOnClickListener {

            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            val request = RegisterRequest(name, email, password)

            RetrofitClient.getApiService(this).register(request)
                .enqueue(object : Callback<ApiResponse<RegisterResponse>> {
                    override fun onResponse(
                        call: Call<ApiResponse<RegisterResponse>>,
                        response: Response<ApiResponse<RegisterResponse>>
                    ) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Response code: ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        if (response.isSuccessful && response.body()?.success == true) {

                            Toast.makeText(
                                this@RegisterActivity,
                                response.body()?.message ?: "Registration successful",
                                Toast.LENGTH_SHORT
                            ).show()

                            val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(
                                this@RegisterActivity,
                                response.body()?.message ?: "Registration failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponse<RegisterResponse>>,
                        t: Throwable
                    ) {
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
