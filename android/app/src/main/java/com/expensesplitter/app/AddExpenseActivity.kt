package com.expensesplitter.app

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.CreateExpenseRequest
import com.expensesplitter.app.model.GroupMemberResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddExpenseActivity : AppCompatActivity() {

    private lateinit var participantAdapter: ParticipantAdapter
    private var groupId: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_expense)

        groupId = intent.getLongExtra("GROUP_ID", 0)

        val etAmount = findViewById<EditText>(R.id.etAmount)
        val etDescription = findViewById<EditText>(R.id.etDescription)
        val spinnerPaidBy = findViewById<Spinner>(R.id.spinnerPaidBy)
        val rvParticipants = findViewById<RecyclerView>(R.id.rvParticipants)
        val btnSave = findViewById<Button>(R.id.btnSaveExpense)

        loadMembers { members ->

            participantAdapter = ParticipantAdapter(members)
            rvParticipants.layoutManager = LinearLayoutManager(this)
            rvParticipants.adapter = participantAdapter

            spinnerPaidBy.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                members.map { member -> member.name }
            )

            btnSave.setOnClickListener {

                val amount = etAmount.text.toString().toDoubleOrNull()
                val description = etDescription.text.toString().trim()

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

                val paidByIndex = spinnerPaidBy.selectedItemPosition
                val paidById = members[paidByIndex].id

                val request = CreateExpenseRequest(
                    description = description,
                    totalAmount = amount,
                    paidBy = paidById,
                    participantIds = participantAdapter.selectedIds.toList()
                )

                createExpense(request)
            }
        }
    }

    private fun createExpense(request: CreateExpenseRequest) {

        RetrofitClient.getApiService(this)
            .createExpense(groupId, request)
            .enqueue(object : Callback<ApiResponse<String>> {

                override fun onResponse(
                    call: Call<ApiResponse<String>>,
                    response: Response<ApiResponse<String>>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        Toast.makeText(
                            this@AddExpenseActivity,
                            response.body()?.message ?: "Expense Created",
                            Toast.LENGTH_SHORT
                        ).show()

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
