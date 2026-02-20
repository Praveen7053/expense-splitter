package com.expensesplitter.app

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.expensesplitter.app.databinding.ActivityExpenseDetailBinding
import com.expensesplitter.app.model.ExpenseResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.recyclerview.widget.GridLayoutManager

class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExpenseDetailBinding
    private lateinit var participantAdapter: ExpenseDetailParticipantAdapter

    private var groupId: Long = -1
    private var expenseId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        expenseId = intent.getLongExtra("EXPENSE_ID", -1)

        setupRecycler()
        fetchExpenseDetail()

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecycler() {

        participantAdapter = ExpenseDetailParticipantAdapter(emptyList())

        binding.recyclerParticipants.apply {
            layoutManager = GridLayoutManager(this@ExpenseDetailActivity, 2)
            adapter = participantAdapter
            setPadding(8, 8, 8, 8)
            clipToPadding = false
        }
    }

    private fun fetchExpenseDetail() {

        RetrofitClient.getApiService(this)
            .getExpenseById(groupId, expenseId)
            .enqueue(object : retrofit2.Callback<ApiResponse<ExpenseResponse>> {

                override fun onResponse(
                    call: retrofit2.Call<ApiResponse<ExpenseResponse>>,
                    response: retrofit2.Response<ApiResponse<ExpenseResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.data?.let {
                            bindExpenseData(it)
                        }
                    } else {
                        Toast.makeText(
                            this@ExpenseDetailActivity,
                            "Unable to load expense",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: retrofit2.Call<ApiResponse<ExpenseResponse>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@ExpenseDetailActivity,
                        "Failed to load",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun bindExpenseData(expense: ExpenseResponse) {

        binding.tvDescription.text = expense.description
        binding.tvAmount.text = formatAmount(expense.totalAmount)
        binding.tvPaidBy.text = "Paid by ${expense.paidByName}"

        binding.tvDate.text = formatDate(expense.expenseDate)

        participantAdapter.updateData(expense.participants)

        updateMyStatus(expense)
    }

    // ---------- Helpers ----------

    private fun formatAmount(amount: Double): String {
        return "₹" + String.format("%.2f", amount)
    }

    private fun formatAmountBig(amount: BigDecimal): String {
        return "₹" + String.format("%.2f", amount)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = input.parse(dateString)
            output.format(date!!)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun updateMyStatus(expense: ExpenseResponse) {

        val myId = getSharedPreferences("app_prefs", MODE_PRIVATE)
            .getLong("USER_ID", -1)

        val me = expense.participants.find { it.userId == myId }

        if (me != null) {

            val net = me.netBalance

            when {
                net.compareTo(BigDecimal.ZERO) > 0 -> {
                    binding.tvMyStatus.text = "You get ${formatAmountBig(net)}"
                    binding.tvMyStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.green)
                    )
                }

                net.compareTo(BigDecimal.ZERO) < 0 -> {
                    binding.tvMyStatus.text =
                        "You owe ${formatAmountBig(net.abs())}"
                    binding.tvMyStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.red)
                    )
                }

                else -> {
                    binding.tvMyStatus.text = "You are settled"
                    binding.tvMyStatus.setTextColor(Color.GRAY)
                }
            }
        }
    }
}
