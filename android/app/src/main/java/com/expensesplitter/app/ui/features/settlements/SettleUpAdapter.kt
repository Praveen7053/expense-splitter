package com.expensesplitter.app.ui.features.settlements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.databinding.ItemSettleUpBinding
import com.expensesplitter.app.model.settlement.SettlementSummaryResponse

class SettleUpAdapter(
    private var summaries: List<SettlementSummaryResponse>,
    private val onSettleClick: (SettlementSummaryResponse) -> Unit
) : RecyclerView.Adapter<SettleUpAdapter.SettleUpViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettleUpViewHolder {
        val binding = ItemSettleUpBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettleUpViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettleUpViewHolder, position: Int) {
        holder.bind(summaries[position])
    }

    override fun getItemCount() = summaries.size

    fun updateData(newSummaries: List<SettlementSummaryResponse>) {
        summaries = newSummaries
        notifyDataSetChanged()
    }

    inner class SettleUpViewHolder(private val binding: ItemSettleUpBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(summary: SettlementSummaryResponse) {
            binding.tvDebtInfo.text = "You owe ${summary.toUserName}"
            binding.tvAmount.text = String.format("₹%.2f", summary.amount)
            binding.btnSettle.setOnClickListener { onSettleClick(summary) }
        }
    }
}
