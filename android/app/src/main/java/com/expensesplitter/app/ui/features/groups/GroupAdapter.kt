package com.expensesplitter.app.ui.features.groups

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.R
import com.expensesplitter.app.databinding.ItemItemGroupBinding
import com.expensesplitter.app.model.groups.GroupListResponse
import kotlin.math.abs

class GroupAdapter(
    private val groups: List<GroupListResponse>,
    private val onClick: (GroupListResponse) -> Unit
) : RecyclerView.Adapter<GroupAdapter.GroupViewHolder>() {

    private val icons = listOf(
        R.drawable.ic_mountain,
        R.drawable.ic_beach,
        R.drawable.ic_cake,
        R.drawable.ic_camera,
        R.drawable.ic_cocktail
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemItemGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val group = groups[position]
        holder.bind(group)
        holder.itemView.setOnClickListener { onClick(group) }
    }

    override fun getItemCount() = groups.size

    inner class GroupViewHolder(private val binding: ItemItemGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(group: GroupListResponse) {
            binding.tvGroupName.text = group.groupName
            binding.ivGroupIcon.setImageResource(getGroupIcon(group))
        }
    }

    private fun getGroupIcon(group: GroupListResponse): Int {
        val hashCode = group.groupId.hashCode()
        val index = abs(hashCode) % icons.size
        return icons[index]
    }
}
