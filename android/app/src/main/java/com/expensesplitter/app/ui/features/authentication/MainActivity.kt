package com.expensesplitter.app.ui.features.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.databinding.AuthenticationActivityMainBinding
import com.expensesplitter.app.model.authentication.LoginRequest
import com.expensesplitter.app.model.authentication.LoginResponse
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import com.expensesplitter.app.ui.features.home.HomeActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {

    private lateinit var binding: AuthenticationActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthenticationActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }


        binding.btnLogin.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

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
