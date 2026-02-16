package com.expensesplitter.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.ExpenseResponse

class ExpenseAdapter(
    private val expenses: List<ExpenseResponse>
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDescription: TextView = view.findViewById(R.id.tvExpenseDescription)
        val tvAmount: TextView = view.findViewById(R.id.tvExpenseAmount)
        val tvPaidBy: TextView = view.findViewById(R.id.tvExpenseMeta)
        val ivIcon: ImageView = view.findViewById(R.id.ivExpenseIcon)
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
    }
}

