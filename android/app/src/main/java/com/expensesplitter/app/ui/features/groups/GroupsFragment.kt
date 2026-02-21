package com.expensesplitter.app.ui.features.groups

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.databinding.GroupsFragmentGroupsBinding
import com.expensesplitter.app.databinding.ItemItemGroupBinding
import com.expensesplitter.app.model.common.ApiResponse
import com.expensesplitter.app.model.groups.GroupListResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupsFragment : Fragment() {

    private var _binding: GroupsFragmentGroupsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = GroupsFragmentGroupsBinding.inflate(inflater, container, false)

        binding.recyclerGroups.layoutManager = LinearLayoutManager(requireContext())

        binding.fabCreateGroup.setOnClickListener {
            context?.let {
                startActivity(Intent(it, CreateGroupActivity::class.java))
            }
        }

        loadGroups()

        return binding.root
    }

    private fun loadGroups() {
        context?.let { ctx ->
            RetrofitClient.getApiService(ctx)
                .getGroupList()
                .enqueue(object : Callback<ApiResponse<List<GroupListResponse>>> {
                    override fun onResponse(
                        call: Call<ApiResponse<List<GroupListResponse>>>,
                        response: Response<ApiResponse<List<GroupListResponse>>>
                    ) {
                        if (!isAdded) return

                        if (response.isSuccessful && response.body()?.success == true) {
                            val groups = response.body()?.data
                            if (groups.isNullOrEmpty()) {
                                binding.recyclerGroups.visibility = View.GONE
                            } else {
                                binding.recyclerGroups.visibility = View.VISIBLE
                                binding.recyclerGroups.adapter = GroupAdapter(groups) { group ->
                                    context?.let {
                                        val intent = Intent(it, GroupDetailsActivity::class.java)
                                        intent.putExtra("GROUP_ID", group.groupId)
                                        intent.putExtra("GROUP_NAME", group.groupName)
                                        startActivity(intent)
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(
                                context,
                                response.body()?.message ?: "Failed to load groups",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<List<GroupListResponse>>>, t: Throwable) {
                        if (!isAdded) return
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}