package com.expensesplitter.app

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.ExpenseParticipantResponse
import java.math.BigDecimal


class ExpenseDetailParticipantAdapter(
    private var list: List<ExpenseParticipantResponse>
) : RecyclerView.Adapter<ExpenseDetailParticipantAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val tvOwed: TextView = view.findViewById(R.id.tvOwed)
        val tvPaid: TextView = view.findViewById(R.id.tvPaid)
        val tvNet: TextView = view.findViewById(R.id.tvNet)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_detail_participant, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = list[position]

        holder.tvName.text = item.userName
        holder.tvInitial.text =
            item.userName.first().toString().uppercase()

        holder.tvOwed.text = "Owes: ₹${item.amountOwed}"
        holder.tvPaid.text = "Paid: ₹${item.amountPaid}"

        val net: BigDecimal = item.netBalance

        when {
            net.compareTo(BigDecimal.ZERO) > 0 -> {
                holder.tvNet.text = "Gets ₹$net"
                holder.tvNet.setTextColor(Color.parseColor("#2ECC71"))
            }

            net.compareTo(BigDecimal.ZERO) < 0 -> {
                holder.tvNet.text = "Owes ₹${net.abs()}"
                holder.tvNet.setTextColor(Color.parseColor("#E74C3C"))
            }

            else -> {
                holder.tvNet.text = "Settled"
                holder.tvNet.setTextColor(Color.GRAY)
            }
        }
    }


    fun updateData(newList: List<ExpenseParticipantResponse>) {
        list = newList
        notifyDataSetChanged()
    }
}

