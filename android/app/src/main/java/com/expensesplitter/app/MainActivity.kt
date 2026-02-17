package com.expensesplitter.app

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.model.LoginRequest
import com.expensesplitter.app.model.LoginResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.content.Intent
import android.widget.TextView
import com.expensesplitter.app.model.apiResponse.ApiResponse


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        btnLogin.setOnClickListener {

            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            val request = LoginRequest(email, password)

            RetrofitClient.getApiService(this).login(request)
                .enqueue(object : Callback<ApiResponse<LoginResponse>> {
                    override fun onResponse(
                        call: Call<ApiResponse<LoginResponse>>,
                        response: Response<ApiResponse<LoginResponse>>
                    ) {
                        if (response.isSuccessful && response.body()?.success == true) {
                            val token = response.body()?.data?.token
                            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            val data = response.body()?.data

                            sharedPref.edit()
                                .putString("token", data?.token)
                                .putLong("USER_ID", data?.userId ?: -1L)
                                .apply()

                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@MainActivity,
                                response.body()?.message ?: "Login failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponse<LoginResponse>>,
                        t: Throwable
                    ) {
                        Toast.makeText(
                            this@MainActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }
}
