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
import com.expensesplitter.app.model.GroupListResponse
import com.expensesplitter.app.model.apiResponse.ApiResponse
import com.expensesplitter.app.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroupsFragment : Fragment() {

    private var recyclerGroups: RecyclerView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_groups, container, false)

        val btnCreateGroup = view.findViewById<Button>(R.id.btnCreateGroup)
        recyclerGroups = view.findViewById(R.id.recyclerGroups)

        recyclerGroups?.layoutManager = LinearLayoutManager(requireContext())

        btnCreateGroup.setOnClickListener {
            context?.let {
                startActivity(Intent(it, CreateGroupActivity::class.java))
            }
        }

        loadGroups()

        return view
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

                        if (!isAdded) return  // 🔥 VERY IMPORTANT

                        if (response.isSuccessful && response.body()?.success == true) {

                            val groups = response.body()?.data

                            Log.d("GROUP_DEBUG", "Total groups: ${groups?.size}")

                            if (groups.isNullOrEmpty()) {
                                recyclerGroups?.visibility = View.GONE
                            } else {

                                recyclerGroups?.visibility = View.VISIBLE

                                recyclerGroups?.adapter =
                                    GroupAdapter(groups) { group ->

                                        context?.let {
                                            val intent = Intent(
                                                it,
                                                GroupDetailsActivity::class.java
                                            )

                                            intent.putExtra("GROUP_ID", group.groupId)
                                            intent.putExtra("GROUP_NAME", group.groupName)

                                            startActivity(intent)
                                        }
                                    }

                                recyclerGroups?.setHasFixedSize(true)
                                recyclerGroups?.overScrollMode = View.OVER_SCROLL_NEVER
                            }

                        } else {

                            context?.let {
                                Toast.makeText(
                                    it,
                                    response.body()?.message ?: "Failed to load groups",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    override fun onFailure(
                        call: Call<ApiResponse<List<GroupListResponse>>>,
                        t: Throwable
                    ) {

                        if (!isAdded) return  // 🔥 VERY IMPORTANT

                        context?.let {
                            Toast.makeText(
                                it,
                                "Error: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerGroups = null   // 🔥 prevents memory leak
    }
}

