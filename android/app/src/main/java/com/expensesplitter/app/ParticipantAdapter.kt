package com.expensesplitter.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.GroupMemberResponse

class ParticipantAdapter(
    private val members: List<GroupMemberResponse>
) : RecyclerView.Adapter<ParticipantAdapter.ViewHolder>() {

    val selectedIds = mutableSetOf<Long>()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: LinearLayout = view.findViewById(R.id.layoutContainer)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_participant, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val member = members[position]

        holder.tvName.text = member.name
        holder.tvInitial.text = member.name.first().toString().uppercase()

        val isSelected = selectedIds.contains(member.id)

        holder.container.setBackgroundResource(
            if (isSelected)
                R.drawable.bg_participant_selected
            else
                R.drawable.bg_participant_unselected
        )

        holder.tvName.setTextColor(
            if (isSelected) android.graphics.Color.WHITE
            else android.graphics.Color.parseColor("#2E2E2E")
        )

        holder.container.setOnClickListener {
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

