package com.expensesplitter.app.ui.features.settlements

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensesplitter.app.databinding.ActivitySettleUpBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.settlement.SettlementSummaryResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SettleUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettleUpBinding
    private lateinit var settleUpAdapter: SettleUpAdapter
    private var groupId: Long = -1

    private val addSettlementLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            fetchSettlementSummary()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettleUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        groupId = intent.getLongExtra("GROUP_ID", -1)

        setupRecyclerView()
        fetchSettlementSummary()

        binding.btnBack.setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        settleUpAdapter = SettleUpAdapter(emptyList()) { summary ->
            val intent = Intent(this, AddSettlementActivity::class.java)
            intent.putExtra("GROUP_ID", groupId)
            intent.putExtra("TO_USER_ID", summary.toUserId)
            intent.putExtra("AMOUNT", summary.amount.toString())
            addSettlementLauncher.launch(intent)
        }
        binding.recyclerSettleUp.apply {
            layoutManager = LinearLayoutManager(this@SettleUpActivity)
            adapter = settleUpAdapter
        }
    }

    private fun fetchSettlementSummary() {
        if (groupId == -1L) {
            Toast.makeText(this, "Invalid group ID", Toast.LENGTH_SHORT).show()
            return
        }

        RetrofitClient.getApiService(this).getSettlementSummary(groupId)
            .enqueue(object : Callback<ApiResponse<List<SettlementSummaryResponse>>> {
                override fun onResponse(
                    call: Call<ApiResponse<List<SettlementSummaryResponse>>>,
                    response: Response<ApiResponse<List<SettlementSummaryResponse>>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val myId = getSharedPreferences("app_prefs", MODE_PRIVATE).getLong("USER_ID", -1)
                        val myDebts = response.body()?.data?.filter { it.fromUserId == myId } ?: emptyList()

                        if (myDebts.isEmpty()) {
                            binding.recyclerSettleUp.visibility = View.GONE
                            binding.tvEmptyMessage.visibility = View.VISIBLE
                        } else {
                            binding.recyclerSettleUp.visibility = View.VISIBLE
                            binding.tvEmptyMessage.visibility = View.GONE
                            settleUpAdapter.updateData(myDebts)
                        }
                    } else {
                        Toast.makeText(this@SettleUpActivity, "Failed to fetch settlement summary", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<SettlementSummaryResponse>>>, t: Throwable) {
                    Toast.makeText(this@SettleUpActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
