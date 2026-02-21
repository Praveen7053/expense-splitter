package com.expensesplitter.app.ui.features.expenses

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.R
import com.expensesplitter.app.databinding.ItemItemExpenseBinding
import com.expensesplitter.app.model.expenses.ExpenseResponse
import java.math.BigDecimal

class ExpenseAdapter(
    private val expenses: List<ExpenseResponse>,
    private val onClick: (ExpenseResponse) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemItemExpenseBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemItemExpenseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = expenses.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expense = expenses[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvExpenseDescription.text = expense.description
            tvExpenseAmount.text = context.getString(R.string.expense_amount, expense.totalAmount.toString())
            tvExpenseMeta.text = context.getString(R.string.paid_by_and_members, expense.paidByName, expense.participants.size)

            // Dynamic Icon Logic (Simple Version)
            when {
                expense.description.contains("rent", true) ->
                    ivExpenseIcon.setImageResource(R.drawable.ic_home)
                expense.description.contains("fuel", true) ->
                    ivExpenseIcon.setImageResource(R.drawable.ic_car)
                expense.description.contains("dinner", true) ||
                        expense.description.contains("food", true) ->
                    ivExpenseIcon.setImageResource(R.drawable.ic_food)
                else ->
                    ivExpenseIcon.setImageResource(R.drawable.ic_activity)
            }

            val currentUserId = getCurrentUserId(context)
            val myParticipant = expense.participants.find { it.userId == currentUserId }

            if (myParticipant != null) {
                val balance = myParticipant.netBalance
                when {
                    balance > BigDecimal.ZERO -> {
                        tvBalanceHint.text = context.getString(R.string.you_get, balance.setScale(2).toString())
                        tvBalanceHint.setTextColor(ContextCompat.getColor(context, R.color.balance_positive))
                    }

                    balance < BigDecimal.ZERO -> {
                        tvBalanceHint.text = context.getString(R.string.you_owe, balance.abs().setScale(2).toString())
                        tvBalanceHint.setTextColor(ContextCompat.getColor(context, R.color.balance_negative))
                    }

                    else -> {
                        tvBalanceHint.text = context.getString(R.string.settled)
                        tvBalanceHint.setTextColor(ContextCompat.getColor(context, R.color.balance_settled))
                    }
                }
            }

            root.setOnClickListener {
                onClick(expense)
            }
        }
    }

    private fun getCurrentUserId(context: Context): Long {
        val sharedPref = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPref.getLong("USER_ID", -1)
    }
}
