package com.expensesplitter.app.ui.features.expenses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.R
import com.expensesplitter.app.databinding.ItemItemParticipantBinding
import com.expensesplitter.app.model.groups.GroupMemberResponse

class ParticipantAdapter(
    private val members: List<GroupMemberResponse>
) : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    val selectedIds = mutableSetOf<Long>()

    inner class ViewHolder(val binding: ItemItemParticipantBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemItemParticipantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val member = members[position]

        holder.binding.tvName.text = member.name
        holder.binding.tvInitial.text = member.name.first().toString().uppercase()

        val isSelected = selectedIds.contains(member.id)

        holder.binding.layoutContainer.setBackgroundResource(
            if (isSelected)
                R.drawable.bg_participant_selected
            else
                R.drawable.bg_participant_unselected
        )

        holder.binding.tvName.setTextColor(
            if (isSelected) android.graphics.Color.WHITE
            else android.graphics.Color.parseColor("#2E2E2E")
        )

        holder.binding.root.setOnClickListener {
            if (isSelected) {
                selectedIds.remove(member.id)
            } else {
                selectedIds.add(member.id)
            }
            notifyItemChanged(position)
        }
    }

    fun selectAll(select: Boolean) {
        selectedIds.clear()
        if (select) {
            members.forEach { selectedIds.add(it.id) }
        }
        notifyDataSetChanged()
    }
}
