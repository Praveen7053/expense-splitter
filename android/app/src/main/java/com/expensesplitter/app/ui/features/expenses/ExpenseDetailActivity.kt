package com.expensesplitter.app.ui.features.expenses

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.expensesplitter.app.R
import com.expensesplitter.app.databinding.ExpensesActivityExpenseDetailBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.expenses.ExpenseResponse
import com.expensesplitter.app.network.RetrofitClient
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseDetailActivity : AppCompatActivity() {

    private lateinit var binding: ExpensesActivityExpenseDetailBinding
    private lateinit var participantAdapter: ExpenseDetailParticipantAdapter

    private var groupId: Long = -1
    private var expenseId: Long = -1

    private val editExpenseLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            fetchExpenseDetail()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExpensesActivityExpenseDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        expenseId = intent.getLongExtra("EXPENSE_ID", -1)

        setupRecycler()
        fetchExpenseDetail()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }

        binding.btnEdit.setOnClickListener {
            val intent = Intent(this, AddExpenseActivity::class.java)
            intent.putExtra("GROUP_ID", groupId)
            intent.putExtra("EXPENSE_ID", expenseId)
            editExpenseLauncher.launch(intent)
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Delete Expense")
            .setMessage("Are you sure you want to delete this expense?")
            .setPositiveButton("Delete") { _, _ -> deleteExpense() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteExpense() {
        RetrofitClient.getApiService(this).deleteExpense(groupId, expenseId)
            .enqueue(object : retrofit2.Callback<ApiResponse<String>> {
                override fun onResponse(
                    call: retrofit2.Call<ApiResponse<String>>,
                    response: retrofit2.Response<ApiResponse<String>>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@ExpenseDetailActivity, "Expense deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@ExpenseDetailActivity, "Failed to delete expense", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse<String>>, t: Throwable) {
                    Toast.makeText(this@ExpenseDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
