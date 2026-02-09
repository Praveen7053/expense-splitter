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

        val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val email = sharedPref.getString("email", "User")
        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottomNav)
        val imgLogout = findViewById<ImageView>(R.id.imgLogout)

        // default screen
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                    true
                }

                R.id.nav_groups -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, GroupsFragment())
                        .commit()
                    true
                }

                R.id.nav_add -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, AddExpenseFragment())
                        .commit()
                    true
                }

                R.id.nav_activity -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ActivityFragment())
                        .commit()
                    true
                }

                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, ProfileFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }

        imgLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
