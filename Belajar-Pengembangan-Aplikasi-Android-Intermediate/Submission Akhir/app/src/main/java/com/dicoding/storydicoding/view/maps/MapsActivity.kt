package com.dicoding.storydicoding.view.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.dicoding.storydicoding.R
import com.dicoding.storydicoding.data.response.ListStoryItem
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.dicoding.storydicoding.databinding.ActivityMapsBinding
import com.dicoding.storydicoding.view.ViewModelFactory
import com.google.android.gms.maps.model.LatLngBounds

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var viewModel: MapsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val factory = ViewModelFactory.getInstance(this)
        viewModel = ViewModelProvider(this, factory)[MapsViewModel::class.java]

        viewModel.storiesWithLocation.observe(this) { stories ->
            if (stories != null) {
                addMarkersToMap(stories)
            }
        }

        viewModel.fetchStoriesWithLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun addMarkersToMap(stories: List<ListStoryItem>) {
        val boundsBuilder = LatLngBounds.Builder()

        stories.forEach { story ->
            val position = LatLng(story.lat ?: 0.0, story.lon ?: 0.0)
            mMap.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(story.name)
                    .snippet(story.description)
            )
            boundsBuilder.include(position)
        }
        if (stories.isNotEmpty()) {
            val bounds = boundsBuilder.build()
            mMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }
}