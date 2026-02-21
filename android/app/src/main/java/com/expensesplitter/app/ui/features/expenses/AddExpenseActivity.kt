package com.expensesplitter.app.ui.features.expenses

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.expensesplitter.app.databinding.ExpensesActivityAddExpenseBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.expenses.CreateExpenseRequest
import com.expensesplitter.app.model.expenses.ExpenseResponse
import com.expensesplitter.app.model.groups.GroupMemberResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var binding: ExpensesActivityAddExpenseBinding
    private lateinit var participantAdapter: ParticipantAdapter
    private var groupId: Long = 0
    private var expenseId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ExpensesActivityAddExpenseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", 0)
        expenseId = intent.getLongExtra("EXPENSE_ID", -1)

        if (expenseId != -1L) {
            binding.btnSaveExpense.text = "Update"
            loadExpenseDetails()
        }

        loadMembers { members ->

            participantAdapter = ParticipantAdapter(members)

            binding.rvParticipants.layoutManager = GridLayoutManager(this, 2)
            binding.rvParticipants.adapter = participantAdapter

            binding.spinnerPaidBy.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                members.map { it.name }
            )

            binding.spinnerPaidBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val name = members[position].name
                    binding.tvPaidByInitial.text = name.first().toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            binding.cbSelectAll.setOnCheckedChangeListener(null)
            binding.cbSelectAll.isChecked = true
            participantAdapter.selectAll(true)

            binding.cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
                participantAdapter.selectAll(isChecked)
            }

            binding.btnSaveExpense.setOnClickListener {

                val amount = binding.etAmount.text.toString().toDoubleOrNull()
                val description = binding.etDescription.text.toString().trim()

                if (amount == null || amount <= 0) {
                    Toast.makeText(this, "Enter valid amount", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (description.isEmpty()) {
                    Toast.makeText(this, "Enter description", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (participantAdapter.selectedIds.isEmpty()) {
                    Toast.makeText(this, "Select at least one participant", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val paidByIndex = binding.spinnerPaidBy.selectedItemPosition
                val paidById = members[paidByIndex].id

                if (!participantAdapter.selectedIds.contains(paidById)) {
                    Toast.makeText(this, "Paid by must be included in split", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val request = CreateExpenseRequest(
                    description = description,
                    totalAmount = amount,
                    paidBy = paidById,
                    participantIds = participantAdapter.selectedIds.toList()
                )

                binding.btnSaveExpense.isEnabled = false
                if (expenseId == -1L) {
                    createExpense(request)
                } else {
                    updateExpense(request)
                }
            }
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadExpenseDetails() {
        RetrofitClient.getApiService(this).getExpenseById(groupId, expenseId)
            .enqueue(object : Callback<ApiResponse<ExpenseResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<ExpenseResponse>>,
                    response: Response<ApiResponse<ExpenseResponse>>
                ) {
                    if (response.isSuccessful && response.body()?.data != null) {
                        val expense = response.body()?.data!!
                        binding.etAmount.setText(expense.totalAmount.toString())
                        binding.etDescription.setText(expense.description)
                        // TODO: Set spinner and participants
                    }
                }

                override fun onFailure(call: Call<ApiResponse<ExpenseResponse>>, t: Throwable) {
                    // TODO: Handle failure
                }
            })
    }

    private fun createExpense(request: CreateExpenseRequest) {

        RetrofitClient.getApiService(this)
            .createExpense(groupId, request)
            .enqueue(object : Callback<ApiResponse<String>> {

                override fun onResponse(
                    call: Call<ApiResponse<String>>,
                    response: Response<ApiResponse<String>>
                ) {

                    binding.btnSaveExpense.isEnabled = true
                    if (response.isSuccessful && response.body()?.success == true) {

                        Toast.makeText(
                            this@AddExpenseActivity,
                            response.body()?.message ?: "Expense Created",
                            Toast.LENGTH_SHORT
                        ).show()

                        setResult(Activity.RESULT_OK)
                        finish()

                    } else {
                        Toast.makeText(
                            this@AddExpenseActivity,
                            response.body()?.message ?: "Failed to create expense",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<String>>,
                    t: Throwable
                ) {
                    binding.btnSaveExpense.isEnabled = true

                    Toast.makeText(
                        this@AddExpenseActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun updateExpense(request: CreateExpenseRequest) {
        RetrofitClient.getApiService(this)
            .updateExpense(groupId, expenseId, request)
            .enqueue(object : Callback<ApiResponse<String>> {
                override fun onResponse(
                    call: Call<ApiResponse<String>>,
                    response: Response<ApiResponse<String>>
                ) {
                    binding.btnSaveExpense.isEnabled = true
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(
                            this@AddExpenseActivity,
                            response.body()?.message ?: "Expense updated",
                            Toast.LENGTH_SHORT
                        ).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddExpenseActivity,
                            response.body()?.message ?: "Failed to update expense",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                    binding.btnSaveExpense.isEnabled = true
                    Toast.makeText(
                        this@AddExpenseActivity,
                        "Network error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun loadMembers(onLoaded: (List<GroupMemberResponse>) -> Unit) {

        RetrofitClient.getApiService(this)
            .getGroupMembers(groupId)
            .enqueue(object : Callback<ApiResponse<List<GroupMemberResponse>>> {

                override fun onResponse(
                    call: Call<ApiResponse<List<GroupMemberResponse>>>,
                    response: Response<ApiResponse<List<GroupMemberResponse>>>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val members = response.body()?.data ?: emptyList()
                        onLoaded(members)

                    } else {
                        Toast.makeText(
                            this@AddExpenseActivity,
                            response.body()?.message ?: "Failed to load members",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<List<GroupMemberResponse>>>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@AddExpenseActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
