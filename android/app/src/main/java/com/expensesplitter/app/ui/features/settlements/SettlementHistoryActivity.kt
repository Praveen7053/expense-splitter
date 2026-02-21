package com.expensesplitter.app.ui.features.settlements

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensesplitter.app.databinding.ActivitySettlementHistoryBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.settlement.SettlementResponse
import com.expensesplitter.app.network.RetrofitClient

class SettlementHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettlementHistoryBinding
    private lateinit var settlementAdapter: SettlementAdapter
    private var groupId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettlementHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", -1)

        setupRecyclerView()
        fetchSettlements()

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        settlementAdapter = SettlementAdapter(emptyList())
        binding.recyclerSettlements.apply {
            layoutManager = LinearLayoutManager(this@SettlementHistoryActivity)
            adapter = settlementAdapter
        }
    }

    private fun fetchSettlements() {
        if (groupId == -1L) {
            Toast.makeText(this, "Invalid group ID", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getApiService(this).getSettlements(groupId)
            .enqueue(object : retrofit2.Callback<ApiResponse<List<SettlementResponse>>> {
                override fun onResponse(
                    call: retrofit2.Call<ApiResponse<List<SettlementResponse>>>,
                    response: retrofit2.Response<ApiResponse<List<SettlementResponse>>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        response.body()?.data?.let {
                            settlementAdapter.updateData(it)
                        }
                    } else {
                        Toast.makeText(this@SettlementHistoryActivity, "Failed to fetch settlements", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse<List<SettlementResponse>>>, t: Throwable) {
                    Toast.makeText(this@SettlementHistoryActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
