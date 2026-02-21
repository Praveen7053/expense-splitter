package com.expensesplitter.app.ui.features.settlements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.databinding.ItemSettlementBinding
import com.expensesplitter.app.model.settlement.SettlementResponse
import java.text.SimpleDateFormat
import java.util.Locale

class SettlementAdapter(private var settlements: List<SettlementResponse>) : RecyclerView.Adapter<SettlementAdapter.SettlementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SettlementViewHolder {
        val binding = ItemSettlementBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SettlementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SettlementViewHolder, position: Int) {
        holder.bind(settlements[position])
    }

    override fun getItemCount() = settlements.size

    fun updateData(newSettlements: List<SettlementResponse>) {
        settlements = newSettlements
        notifyDataSetChanged()
    }

    inner class SettlementViewHolder(private val binding: ItemSettlementBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(settlement: SettlementResponse) {
            binding.tvSettlementInfo.text = "${settlement.paidByName} paid ${settlement.paidToName}"
            binding.tvAmount.text = String.format("₹%.2f", settlement.amount)
            binding.tvDate.text = formatDate(settlement.createdAt)
        }

        private fun formatDate(dateString: String): String {
            return try {
                val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val output = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                val date = input.parse(dateString)
                output.format(date!!)
            } catch (e: Exception) {
                dateString
            }
        }
    }
}
