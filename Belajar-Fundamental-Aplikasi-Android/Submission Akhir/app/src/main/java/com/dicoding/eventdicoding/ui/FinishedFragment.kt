package com.dicoding.eventdicoding.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.eventdicoding.R
import com.dicoding.eventdicoding.data.response.EventResponse
import com.dicoding.eventdicoding.data.retrofit.ApiConfig
import com.dicoding.eventdicoding.databinding.FragmentFinishedBinding
import com.dicoding.eventdicoding.ui.detail.DetailEventActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FinishedFragment : Fragment() {

    private var _binding: FragmentFinishedBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFinishedBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvEvents.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(requireActivity(), layoutManager.orientation)
        binding.rvEvents.addItemDecoration(itemDecoration)

        val eventAdapter = EventAdapter { selectedEvent ->
            val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", selectedEvent.id.toString())
            }
            startActivity(intent)
        }
        binding.rvEvents.adapter = eventAdapter

        loadEvents(eventAdapter)
    }

    private fun loadEvents(adapter: EventAdapter) {
        showLoading(true)
        val client = ApiConfig.getApiService().getFinishedEvents()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val eventList = response.body()?.listEvents ?: emptyList()
                    adapter.submitList(eventList)
                } else {
                    // Handle failure
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                showLoading(false)
                // Handle error
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}