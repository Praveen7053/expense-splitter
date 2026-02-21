package com.expensesplitter.app.ui.features.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.databinding.ItemItemGroupBinding
import com.expensesplitter.app.model.groups.GroupListResponse

class GroupAdapter(
    private val groups: List<GroupListResponse>,
    private val onClick: (GroupListResponse) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    class GroupViewHolder(val binding: ItemItemGroupBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.binding.tvGroupName.text = group.groupName

        holder.binding.root.setOnClickListener {
            onClick(group)
        }
    }

    override fun getItemCount(): Int = groups.size
}
