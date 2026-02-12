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

        val groupId = intent.getLongExtra("GROUP_ID", -1)
        val groupName = intent.getStringExtra("GROUP_NAME")

        val tvTitle = findViewById<TextView>(R.id.tvGroupTitle)
        tvTitle.text = groupName

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<ImageView>(R.id.btnAddMember).setOnClickListener {

            val addIntent = Intent(this, AddMemberActivity::class.java)
            addIntent.putExtra("GROUP_ID", groupId)
            addIntent.putExtra("GROUP_NAME", groupName)
            startActivity(addIntent)
        }
    }
}
