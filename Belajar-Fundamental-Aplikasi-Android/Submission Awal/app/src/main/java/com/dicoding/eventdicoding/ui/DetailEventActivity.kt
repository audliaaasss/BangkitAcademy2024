package com.dicoding.eventdicoding.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dicoding.eventdicoding.data.response.DetailEventResponse
import com.dicoding.eventdicoding.data.response.Event
import com.dicoding.eventdicoding.data.retrofit.ApiConfig
import com.dicoding.eventdicoding.databinding.ActivityDetailEventBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get event ID from Intent
        val eventId = intent.getStringExtra("EVENT_ID") ?: return
        Log.d("DetailEventActivity", "Event ID: $eventId")

        showLoading(true)

        // Fetch event details
        loadEventDetail(eventId)

        // Button to open link
        binding.btnOpenLink.setOnClickListener {
            val eventLink = binding.tvEventLink.text.toString()
            openEventLink(eventLink)
        }
    }

    private fun loadEventDetail(id: String) {
        val client = ApiConfig.getApiService().getDetailEvent(id)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                if (response.isSuccessful) {
                    val event = response.body()?.event
                    if (event != null) {
                        displayEventDetails(event)
                        showLoading(false)
                    } else {
                        Log.e("DetailEventActivity", "Event not found")
                        showLoading(false)
                    }
                } else {
                    Log.e("EventDetailActivity", "Failed to load event: ${response.message()}")
                    showLoading(false)
                }
            }

            override fun onFailure(call: Call<DetailEventResponse>, t: Throwable) {
                Log.e("EventDetailActivity", "Error: ${t.message}")
                showLoading(false)
            }
        })
    }

    private fun displayEventDetails(event: Event) {
        // Load event image
        Glide.with(this)
            .load(event.mediaCover ?: event.imageLogo)
            .into(binding.ivEventImage)

        // Set event name, organizer, time, remaining quota
        binding.tvEventName.text = event.name
        binding.tvEventOrganizer.text = event.ownerName
        binding.tvEventTime.text = "${event.beginTime} - ${event.endTime}"

        val remainingQuota = event.quota?.minus(event.registrants ?: 0)
        binding.tvEventQuota.text = "Remaining quota: $remainingQuota"

        // Set event description as HTML
        binding.tvEventDescription.text = HtmlCompat.fromHtml(
            event.description.toString(),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        // Set event link
        binding.tvEventLink.text = event.link
    }

    private fun openEventLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}