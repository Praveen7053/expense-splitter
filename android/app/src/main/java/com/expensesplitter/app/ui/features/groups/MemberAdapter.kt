package com.expensesplitter.app.ui.features.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.databinding.ItemItemMemberBinding
import com.expensesplitter.app.model.groups.GroupMemberResponse

class MemberAdapter(
    private val members: List<GroupMemberResponse>
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemItemMemberBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemItemMemberBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = members.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val member = members[position]
        holder.binding.tvMemberName.text = member.name
        holder.binding.tvMemberEmail.text = member.email
    }
}
