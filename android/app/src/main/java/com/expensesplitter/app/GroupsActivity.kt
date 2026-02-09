package com.expensesplitter.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class GroupsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_groups)

        val imgBack = findViewById<ImageView>(R.id.imgBack)

        imgBack.setOnClickListener {
            finish()
        }
    }
}
