package com.expensesplitter.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.GroupMemberResponse

class MemberHorizontalAdapter(
    private val members: List<GroupMemberResponse>
) : RecyclerView.Adapter<MemberHorizontalAdapter.ViewHolder>() {

    private val colors = listOf(
        "#7E57C2",
        "#5C6BC0",
        "#26A69A",
        "#EF5350",
        "#FF7043",
        "#42A5F5"
    )

    init {
        setHasStableIds(true)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val tvName: TextView = view.findViewById(R.id.tvName)
    }

    override fun getItemId(position: Int): Long {
        return members[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_member_avatar, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]

        holder.tvName.text = member.name

        val initial = member.name
            ?.firstOrNull()
            ?.uppercase()
            ?: "?"

        holder.tvInitial.text = initial

        val color = android.graphics.Color.parseColor(
            colors[position % colors.size]
        )

        holder.tvInitial.setBackgroundColor(color)
    }
}

