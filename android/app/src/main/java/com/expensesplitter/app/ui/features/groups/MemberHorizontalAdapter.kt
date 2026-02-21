package com.expensesplitter.app.ui.features.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.databinding.ItemItemMemberAvatarBinding
import com.expensesplitter.app.model.groups.GroupMemberResponse

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

    class ViewHolder(val binding: ItemItemMemberAvatarBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemId(position: Int): Long {
        return members[position].id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemItemMemberAvatarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]

        holder.binding.tvName.text = member.name

        val initial = member.name
            ?.firstOrNull()
            ?.uppercase()
            ?: "?"

        holder.binding.tvInitial.text = initial

        val color = android.graphics.Color.parseColor(
            colors[position % colors.size]
        )

        holder.binding.tvInitial.setBackgroundColor(color)
    }
}
