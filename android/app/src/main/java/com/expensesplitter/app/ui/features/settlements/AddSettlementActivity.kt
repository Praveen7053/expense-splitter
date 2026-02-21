package com.expensesplitter.app.ui.features.settlements

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.expensesplitter.app.databinding.ActivityAddSettlementBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.groups.GroupMemberResponse
import com.expensesplitter.app.model.settlement.CreateSettlementRequest
import com.expensesplitter.app.network.RetrofitClient
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.math.BigDecimal

class AddSettlementActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddSettlementBinding
    private var groupId: Long = -1
    private lateinit var members: List<GroupMemberResponse>
    private var toUserId: Long = -1
    private var amount: BigDecimal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddSettlementBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", -1)
        toUserId = intent.getLongExtra("TO_USER_ID", -1)
        val amountString = intent.getStringExtra("AMOUNT")
        if (amountString != null) {
            amount = BigDecimal(amountString)
        }

        fetchGroupMembers()

        binding.btnBack.setOnClickListener { finish() }

        binding.btnAddSettlement.setOnClickListener { addSettlement() }
    }

    private fun fetchGroupMembers() {
        if (groupId == -1L) {
            Toast.makeText(this, "Invalid group ID", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getApiService(this).getGroupMembers(groupId)
            .enqueue(object : Callback<ApiResponse<List<GroupMemberResponse>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<GroupMemberResponse>>>,
                    response: Response<ApiResponse<List<GroupMemberResponse>>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        members = response.body()?.data ?: emptyList()
                        setupSpinner()
                        prefillData()
                    } else {
                        Toast.makeText(this@AddSettlementActivity, "Failed to fetch members", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<GroupMemberResponse>>>, t: Throwable) {
                    Toast.makeText(this@AddSettlementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun setupSpinner() {
        val memberNames = members.map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, memberNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerReceiver.adapter = adapter
    }

    private fun prefillData() {
        if (toUserId != -1L) {
            val memberIndex = members.indexOfFirst { it.id == toUserId }
            if (memberIndex != -1) {
                binding.spinnerReceiver.setSelection(memberIndex)
                binding.spinnerReceiver.isEnabled = false
            }
        }
        if (amount != null) {
            binding.etAmount.setText(amount.toString())
            binding.etAmount.isEnabled = false
        }
    }

    private fun addSettlement() {
        val receiverPosition = binding.spinnerReceiver.selectedItemPosition
        val amount = binding.etAmount.text.toString().toBigDecimalOrNull()

        if (amount == null || amount <= BigDecimal.ZERO) {
            Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val receiver = members[receiverPosition]

        val myId = getSharedPreferences("app_prefs", MODE_PRIVATE).getLong("USER_ID", -1)
        if (receiver.id == myId) {
            Toast.makeText(this, "You cannot settle with yourself", Toast.LENGTH_SHORT).show()
            return
        }

        val request = CreateSettlementRequest(
            paidToUserId = receiver.id,
            amount = amount
        )

        RetrofitClient.getApiService(this).createSettlement(groupId, request)
            .enqueue(object : Callback<ApiResponse<String>> {
                override fun onResponse(call: Call<ApiResponse<String>>, response: Response<ApiResponse<String>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AddSettlementActivity, "Settlement added", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        try {
                            val errorBody = response.errorBody()?.string()
                            if (errorBody != null) {
                                val gson = Gson()
                                val apiResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                                Toast.makeText(this@AddSettlementActivity, apiResponse.message, Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(this@AddSettlementActivity, "Failed to add settlement", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@AddSettlementActivity, "Failed to parse error response", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ApiResponse<String>>, t: Throwable) {
                    Toast.makeText(this@AddSettlementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
