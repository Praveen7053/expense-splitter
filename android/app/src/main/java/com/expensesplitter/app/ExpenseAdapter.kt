package com.expensesplitter.app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.ExpenseResponse
import android.graphics.Color
import android.util.Log
import java.math.BigDecimal


class ExpenseAdapter(
    private val expenses: List<ExpenseResponse>,
    private val onClick: (ExpenseResponse) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescription: TextView = view.findViewById(R.id.tvExpenseDescription)
        val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
        val tvPaidBy: TextView = view.findViewById(R.id.tvExpenseMeta)
        val ivIcon: ImageView = view.findViewById(R.id.ivExpenseIcon)
        val tvBalanceHint: TextView = view.findViewById(R.id.tvBalanceHint)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = expenses.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val expense = expenses[position]

        holder.tvDescription.text = expense.description
        holder.tvAmount.text = "₹${expense.totalAmount}"
        holder.tvPaidBy.text = "Paid by ${expense.paidByName} • ${expense.participants.size} members"

        // 🔥 Dynamic Icon Logic (Simple Version)
        when {
            expense.description.contains("rent", true) ->
                holder.ivIcon.setImageResource(R.drawable.ic_home)

            expense.description.contains("fuel", true) ->
                holder.ivIcon.setImageResource(R.drawable.ic_car)

            expense.description.contains("dinner", true) ||
                    expense.description.contains("food", true) ->
                holder.ivIcon.setImageResource(R.drawable.ic_food)

            else ->
                holder.ivIcon.setImageResource(R.drawable.ic_activity)
        }

        val currentUserId = getCurrentUserId(holder.itemView.context)

        val myParticipant = expense.participants.find {
            it.userId == currentUserId
        }

        if (myParticipant != null) {

            val balance = myParticipant.netBalance
            Log.d("ExpenseCheck", "${expense.description} -> ${myParticipant?.netBalance}")

            when {
                balance > BigDecimal.ZERO -> {
                    holder.tvBalanceHint.text =
                        "You get ₹${balance.setScale(2)}"
                    holder.tvBalanceHint.setTextColor(Color.parseColor("#2E7D32"))
                }

                balance < BigDecimal.ZERO -> {
                    holder.tvBalanceHint.text =
                        "You owe ₹${balance.abs().setScale(2)}"
                    holder.tvBalanceHint.setTextColor(Color.parseColor("#E53935"))
                }

                else -> {
                    holder.tvBalanceHint.text = "Settled"
                    holder.tvBalanceHint.setTextColor(Color.parseColor("#9E9E9E"))
                }
            }
        }

        holder.itemView.setOnClickListener {
            onClick(expense)
        }
    }

    private fun getCurrentUserId(context: Context): Long {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1)
    }

}

