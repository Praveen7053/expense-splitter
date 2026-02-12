package com.expensesplitter.app

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GroupDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_details)

        val groupName = intent.getStringExtra("GROUP_NAME")

        val tvTitle = findViewById<TextView>(R.id.tvGroupTitle)
        tvTitle.text = groupName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAddMember).setOnClickListener {
            val intent = Intent(this, AddMemberActivity::class.java)
            intent.putExtra("GROUP_ID", intent.getLongExtra("GROUP_ID", -1))
            startActivity(intent)
        }
    }
}
