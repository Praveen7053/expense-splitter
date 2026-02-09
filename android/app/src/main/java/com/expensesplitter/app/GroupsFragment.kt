package com.expensesplitter.app

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.GroupMember
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class GroupsFragment : Fragment() {

    private lateinit var tvEmpty: TextView
    private lateinit var recyclerGroups: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        val btnCreateGroup = view.findViewById<Button>(R.id.btnCreateGroup)
        tvEmpty = view.findViewById(R.id.tvEmpty)
        recyclerGroups = view.findViewById(R.id.recyclerGroups)

        btnCreateGroup.setOnClickListener {
            startActivity(Intent(requireContext(), CreateGroupActivity::class.java))
        }

        loadGroups()

        return view
    }

    private fun loadGroups() {
        RetrofitClient.getApiService(requireContext())
            .getGroupList()
            .enqueue(object : Callback<List<GroupMember>> {

                override fun onResponse(
                    call: Call<List<GroupMember>>,
                    response: Response<List<GroupMember>>
                ) {
                    if (response.isSuccessful) {
                        val groups = response.body()

                        if (groups.isNullOrEmpty()) {
                            tvEmpty.visibility = View.VISIBLE
                            recyclerGroups.visibility = View.GONE
                        } else {
                            tvEmpty.visibility = View.GONE
                            recyclerGroups.visibility = View.VISIBLE
                            recyclerGroups.adapter = GroupAdapter(groups)
                        }
                    } else {
                        tvEmpty.text = "Failed to load groups"
                    }
                }

                override fun onFailure(call: Call<List<GroupMember>>, t: Throwable) {
                    tvEmpty.text = "Error loading groups"
                }
            })
    }
}
