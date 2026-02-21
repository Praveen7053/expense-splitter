package com.expensesplitter.app.ui.features.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.R

open class BaseActivity : AppCompatActivity() {

    private var progressBar: View? = null

    fun showProgressBar() {
        if (progressBar == null) {
            val inflater = LayoutInflater.from(this)
            progressBar = inflater.inflate(R.layout.progress_bar, null)
            val rootView = findViewById<ViewGroup>(android.R.id.content)
            rootView.addView(progressBar)
        }
        progressBar?.visibility = View.VISIBLE
    }

    fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}