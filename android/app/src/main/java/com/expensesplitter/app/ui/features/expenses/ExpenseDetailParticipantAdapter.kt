package com.expensesplitter.app.ui.features.expenses

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.R
import com.expensesplitter.app.databinding.ItemItemExpenseDetailParticipantBinding
import com.expensesplitter.app.model.expenses.ExpenseParticipantResponse
import java.math.BigDecimal

class ExpenseDetailParticipantAdapter(
    private var list: List<ExpenseParticipantResponse>
) : RecyclerView.Adapter<ExpenseDetailParticipantAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemItemExpenseDetailParticipantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemItemExpenseDetailParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.binding.tvInitial.text =
            item.userName.first().toString().uppercase()

        holder.binding.tvName.text = item.userName

        val net = item.netBalance

        when {
            net.compareTo(BigDecimal.ZERO) > 0 -> {
                holder.binding.tvNet.text =
                    "Gets ₹${net.stripTrailingZeros().toPlainString()}"
                holder.binding.tvNet.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.text_positive)
                )
            }

            net.compareTo(BigDecimal.ZERO) < 0 -> {
                holder.binding.tvNet.text =
                    "Owes ₹${net.abs().stripTrailingZeros().toPlainString()}"
                holder.binding.tvNet.setTextColor(
                    ContextCompat.getColor(holder.itemView.context, R.color.text_negative)
                )
            }

            else -> {
                holder.binding.tvNet.text = "Settled"
                holder.binding.tvNet.setTextColor(Color.GRAY)
            }
        }
    }

    fun updateData(newList: List<ExpenseParticipantResponse>) {
        list = newList
        notifyDataSetChanged()
    }
}