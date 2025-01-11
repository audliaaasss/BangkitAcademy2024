package com.dicoding.eventdicoding.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.eventdicoding.data.response.EventResponse
import com.dicoding.eventdicoding.data.retrofit.ApiConfig
import com.dicoding.eventdicoding.databinding.FragmentHomeBinding
import com.dicoding.eventdicoding.ui.EventAdapter
import com.dicoding.eventdicoding.ui.detail.DetailEventActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        binding.apply {
            // Setup upcoming events RecyclerView
            val layoutManagerUpcoming = LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)
            rvUpcomingEvents.layoutManager = layoutManagerUpcoming
            val itemDecorationUpcoming = DividerItemDecoration(requireActivity(), layoutManagerUpcoming.orientation)
            rvUpcomingEvents.addItemDecoration(itemDecorationUpcoming)

            val upcomingEventAdapter = EventAdapter { selectedEvent ->
                val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                    putExtra("EVENT_ID", selectedEvent.id.toString())
                }
                startActivity(intent)
            }
            rvUpcomingEvents.adapter = upcomingEventAdapter

            loadUpcomingEvents(upcomingEventAdapter)

            // Setup finished events RecyclerView
            val layoutManagerFinished = LinearLayoutManager(requireActivity())
            rvFinishedEvents.layoutManager = layoutManagerFinished
            val itemDecorationFinished = DividerItemDecoration(requireActivity(), layoutManagerFinished.orientation)
            rvFinishedEvents.addItemDecoration(itemDecorationFinished)

            val finishedEventAdapter = EventAdapter { selectedEvent ->
                val intent = Intent(requireContext(), DetailEventActivity::class.java).apply {
                    putExtra("EVENT_ID", selectedEvent.id.toString())
                }
                startActivity(intent)
            }
            rvFinishedEvents.adapter = finishedEventAdapter

            loadFinishedEvents(finishedEventAdapter)
        }
    }

    private fun loadUpcomingEvents(adapter: EventAdapter) {
        showLoading(true)
        val client = ApiConfig.getApiService().getActiveEvents()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (_binding != null) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val eventList = response.body()?.listEvents ?: emptyList()
                        adapter.submitList(eventList)
                    } else {
                        // Handle failure
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                if (_binding != null) {
                    showLoading(false)
                    // Handle error
                }
            }
        })
    }

    private fun loadFinishedEvents(adapter: EventAdapter) {
        showLoading(true)
        val client = ApiConfig.getApiService().getFinishedEvents()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                if (_binding != null) {
                    showLoading(false)
                    if (response.isSuccessful) {
                        val eventList = response.body()?.listEvents ?: emptyList()
                        adapter.submitList(eventList)
                    } else {
                        // Handle failure
                    }
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                if (_binding != null) {
                    showLoading(false)
                    // Handle error
                }
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        if (_binding != null) {
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}