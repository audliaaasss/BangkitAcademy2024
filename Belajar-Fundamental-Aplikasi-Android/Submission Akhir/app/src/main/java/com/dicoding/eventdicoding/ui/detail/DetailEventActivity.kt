package com.dicoding.eventdicoding.ui.detail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.dicoding.eventdicoding.R
import com.dicoding.eventdicoding.data.database.FavoriteEvent
import com.dicoding.eventdicoding.data.database.FavoriteEventDatabase
import com.dicoding.eventdicoding.data.response.DetailEventResponse
import com.dicoding.eventdicoding.data.response.Event
import com.dicoding.eventdicoding.data.retrofit.ApiConfig
import com.dicoding.eventdicoding.databinding.ActivityDetailEventBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailEventActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailEventBinding
    private var isFavorite: Boolean = false
    private lateinit var database: FavoriteEventDatabase
    private var currentEvent: Event? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDetailEventBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FavoriteEventDatabase.getDatabase(this)

        // Get event ID from Intent
        val eventId = intent.getStringExtra("EVENT_ID") ?: return
        Log.d("DetailEventActivity", "Event ID: $eventId")

        showLoading(true)

        // Fetch event details
        loadEventDetail(eventId)

        // Check if the event is already in favorites
        checkIfFavorite(eventId)

        // Button to open link
        binding.btnOpenLink.setOnClickListener {
            val eventLink = binding.tvEventLink.text.toString()
            openEventLink(eventLink)
        }

        binding.btnOpenLink.isEnabled = false

        binding.fabFavorite.setOnClickListener {
            toggleFavorite(eventId)
        }
    }

    private fun loadEventDetail(id: String) {
        val client = ApiConfig.getApiService().getDetailEvent(id)
        client.enqueue(object : Callback<DetailEventResponse> {
            override fun onResponse(call: Call<DetailEventResponse>, response: Response<DetailEventResponse>) {
                if (response.isSuccessful) {
                    val event = response.body()?.event
                    if (event != null) {
                        currentEvent = event
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
        binding.btnOpenLink.isEnabled = !event.link.isNullOrEmpty()
    }

    private fun openEventLink(link: String) {
        if (link.isNotEmpty()) {
            val formattedLink = if (!link.startsWith("http://") && !link.startsWith("https://")) {
                "https://$link"
            } else {
                link
            }

            // Intent untuk membuka link di browser
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedLink)).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
            }

            // Cek apakah Chrome terpasang
            val chromePackage = "com.android.chrome"
            val chromeAvailable = packageManager.getLaunchIntentForPackage(chromePackage) != null

            if (chromeAvailable) {
                // Gunakan Chrome jika tersedia
                browserIntent.setPackage(chromePackage)
                startActivity(browserIntent)
            } else {
                // Fallback ke Custom Tabs jika Chrome tidak tersedia
                try {
                    openInCustomTab(formattedLink)
                } catch (e: Exception) {
                    Log.e("DetailEventActivity", "Custom Tabs tidak tersedia: ${e.message}")
                }
            }

        } else {
            Log.e("DetailEventActivity", "Event link is empty.")
            Toast.makeText(this, "Event link is empty.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openInCustomTab(url: String) {
        val builder = androidx.browser.customtabs.CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun checkIfFavorite(eventId: String) {
        lifecycleScope.launch {
            val favoriteEvent = database.favoriteEventDao().getFavoriteEventById(eventId)
            isFavorite = favoriteEvent != null
            updateFavoriteIcon()
        }
    }

    private fun toggleFavorite(eventId: String) {
        lifecycleScope.launch {
            if (isFavorite) {
                // Remove from favorites
                database.favoriteEventDao().deleteById(eventId)
            } else {
                // Add to favorites
                currentEvent?.let { event ->
                    database.favoriteEventDao().insert(
                        FavoriteEvent(
                            id = event.id ?: "",
                            name = event.name ?: "",
                            mediaCover = event.mediaCover
                        )
                    )
                }
            }
            isFavorite = !isFavorite
            updateFavoriteIcon()
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.fabFavorite.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}