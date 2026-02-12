package com.expensesplitter.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.expensesplitter.app.model.GroupMember
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupsFragment : Fragment() {
    private lateinit var recyclerGroups: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        val btnCreateGroup = view.findViewById<Button>(R.id.btnCreateGroup)
        recyclerGroups = view.findViewById(R.id.recyclerGroups)

        recyclerGroups.layoutManager = LinearLayoutManager(requireContext())

        btnCreateGroup.setOnClickListener {
            startActivity(Intent(requireContext(), CreateGroupActivity::class.java))
        }

        loadGroups()

        return view
    }

    private fun loadGroups() {

        RetrofitClient.getApiService(requireContext())
            .getGroupList()
            .enqueue(object : Callback<ApiResponse<List<GroupMember>>> {

                override fun onResponse(
                    call: Call<ApiResponse<List<GroupMember>>>,
                    response: Response<ApiResponse<List<GroupMember>>>
                ) {

                    if (response.isSuccessful && response.body()?.success == true) {

                        val groups = response.body()?.data

                        Log.d("GROUP_DEBUG", "Total groups: ${groups?.size}")

                        if (groups.isNullOrEmpty()) {
                            recyclerGroups.visibility = View.GONE
                        } else {
                            recyclerGroups.visibility = View.VISIBLE

                            recyclerGroups.adapter = GroupAdapter(groups) { group ->

                                val intent = Intent(
                                    requireContext(),
                                    GroupDetailsActivity::class.java
                                )

                                intent.putExtra("GROUP_ID", group.id)
                                intent.putExtra("GROUP_NAME", group.name)

                                startActivity(intent)
                            }

                            recyclerGroups.setHasFixedSize(true)
                            recyclerGroups.overScrollMode = View.OVER_SCROLL_NEVER
                        }

                    } else {

                        Toast.makeText(
                            requireContext(),
                            response.body()?.message ?: "Failed to load groups",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<ApiResponse<List<GroupMember>>>,
                    t: Throwable
                ) {

                    Toast.makeText(
                        requireContext(),
                        "Error: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}
