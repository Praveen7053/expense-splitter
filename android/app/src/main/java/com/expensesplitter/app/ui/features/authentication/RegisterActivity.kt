package com.expensesplitter.app.ui.features.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.expensesplitter.app.databinding.AuthenticationActivityRegisterBinding
import com.expensesplitter.app.model.authentication.RegisterRequest
import com.expensesplitter.app.model.authentication.RegisterResponse
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import com.expensesplitter.app.ui.features.base.BaseActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : BaseActivity() {

    private lateinit var binding: AuthenticationActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthenticationActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegister.setOnClickListener {

            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            val request = RegisterRequest(name, email, password)

            showProgressBar()
            RetrofitClient.getApiService(this).register(request)
                .enqueue(object : Callback<ApiResponse<RegisterResponse>> {
                    override fun onResponse(
                        call: Call<ApiResponse<RegisterResponse>>,
                        response: Response<ApiResponse<RegisterResponse>>
                    ) {
                        hideProgressBar()
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
                        hideProgressBar()
                        Toast.makeText(
                            this@RegisterActivity,
                            "Error: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
