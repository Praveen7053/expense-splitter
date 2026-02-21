package com.expensesplitter.app.ui.features.settlements

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensesplitter.app.databinding.ActivitySettlementsBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.settlement.SettlementResponse
import com.expensesplitter.app.network.RetrofitClient

class SettlementActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettlementsBinding
    private lateinit var settlementAdapter: SettlementAdapter
    private var groupId: Long = -1

    private val addSettlementLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            fetchSettlements()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettlementsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", -1)

        setupRecyclerView()
        fetchSettlements()

        binding.btnBack.setOnClickListener { finish() }

        binding.fabAddSettlement.setOnClickListener {
            val intent = Intent(this, AddSettlementActivity::class.java)
            intent.putExtra("GROUP_ID", groupId)
            addSettlementLauncher.launch(intent)
        }
    }

    private fun setupRecyclerView() {
        settlementAdapter = SettlementAdapter(emptyList())
        binding.recyclerSettlements.apply {
            layoutManager = LinearLayoutManager(this@SettlementActivity)
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
                        Toast.makeText(this@SettlementActivity, "Failed to fetch settlements", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: retrofit2.Call<ApiResponse<List<SettlementResponse>>>, t: Throwable) {
                    Toast.makeText(this@SettlementActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
