package com.expensesplitter.app.ui.features.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.R
import com.expensesplitter.app.databinding.HomeActivityHomeBinding
import com.expensesplitter.app.ui.features.activity.ActivityFragment
import com.expensesplitter.app.ui.features.authentication.MainActivity
import com.expensesplitter.app.ui.features.expenses.AddExpenseFragment
import com.expensesplitter.app.ui.features.groups.GroupsFragment
import com.expensesplitter.app.ui.features.profile.ProfileFragment

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: HomeActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = HomeActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default screen
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, HomeFragment())
            .commit()

        binding.bottomNav.setOnItemSelectedListener { item ->
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

        binding.imgLogout.setOnClickListener {
            val sharedPref = getSharedPreferences("app_prefs", MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
