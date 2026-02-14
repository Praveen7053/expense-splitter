package com.expensesplitter.app

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.GridLayoutManager
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
        val cbSelectAll = findViewById<SwitchCompat>(R.id.cbSelectAll)

        loadMembers { members ->

            participantAdapter = ParticipantAdapter(members)

            rvParticipants.layoutManager = GridLayoutManager(this, 2)
            rvParticipants.adapter = participantAdapter

            spinnerPaidBy.adapter = ArrayAdapter(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                members.map { it.name }
            )

            val tvInitial = findViewById<TextView>(R.id.tvPaidByInitial)

            spinnerPaidBy.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val name = members[position].name
                    tvInitial.text = name.first().toString()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            cbSelectAll.setOnCheckedChangeListener(null)
            cbSelectAll.isChecked = true
            participantAdapter.selectAll(true)

            cbSelectAll.setOnCheckedChangeListener { _, isChecked ->
                participantAdapter.selectAll(isChecked)
            }

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

                btnSave.isEnabled = false
                createExpense(request, btnSave)
            }
        }

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun createExpense(request: CreateExpenseRequest, btnSave: Button) {

        RetrofitClient.getApiService(this)
            .createExpense(groupId, request)
            .enqueue(object : Callback<ApiResponse<String>> {

                override fun onResponse(
                    call: Call<ApiResponse<String>>,
                    response: Response<ApiResponse<String>>
                ) {

                    btnSave.isEnabled = true
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
                    btnSave.isEnabled = true

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
