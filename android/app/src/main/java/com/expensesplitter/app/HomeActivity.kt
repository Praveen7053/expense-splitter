package com.expensesplitter.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val logout = findViewById<ImageView>(R.id.imgLogout)

        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val email = sharedPref.getString("email", "User")

        tvWelcome.text = email

        logout.setOnClickListener {
            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
