package com.dicoding.eventdicoding.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.eventdicoding.R
import com.dicoding.eventdicoding.data.response.EventResponse
import com.dicoding.eventdicoding.data.retrofit.ApiConfig
import com.dicoding.eventdicoding.databinding.ActivityMainBinding
import com.dicoding.eventdicoding.ui.detail.DetailEventActivity
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvEvents.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvEvents.addItemDecoration(itemDecoration)

        val eventAdapter = EventAdapter { selectedEvent ->
            val intent = Intent(this, DetailEventActivity::class.java).apply {
                putExtra("EVENT_ID", selectedEvent.id.toString())
            }
            startActivity(intent)
        }
        binding.rvEvents.adapter = eventAdapter

        loadEvents(eventAdapter)
    }

    private fun loadEvents(adapter: EventAdapter) {
        showLoading(true)
        val client = ApiConfig.getApiService().getActiveEvents()
        client.enqueue(object : Callback<EventResponse> {
            override fun onResponse(call: Call<EventResponse>, response: Response<EventResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    val eventList = response.body()?.listEvents ?: emptyList()
                    adapter.submitList(eventList)
                } else {
                    Log.e("MainActivity", "onFailure: ${response.message()}")
                    Toast.makeText(this@MainActivity, "Failed to load events", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EventResponse>, t: Throwable) {
                showLoading(false)
                Log.e("MainActivity", "onFailure: ${t.message}")
                Toast.makeText(this@MainActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}