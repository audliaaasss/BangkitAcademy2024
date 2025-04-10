package com.dicoding.storydicoding.view.detail

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.dicoding.storydicoding.R
import com.dicoding.storydicoding.databinding.ActivityDetailBinding
import com.dicoding.storydicoding.view.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val storyId = intent.getStringExtra("STORY_ID")

        if (storyId != null) {
            observeDetailStory(storyId)
        }
    }

    private fun observeDetailStory(storyId: String) {
        viewModel.getDetailStory(storyId).observe(this) { story ->
            story?.let {
                binding.progressBar.visibility = View.GONE
                binding.tvDetailName.text = it.name
                binding.tvDetailDescription.text = it.description
                Glide.with(this)
                    .load(it.photoUrl)
                    .into(binding.ivDetailPhoto)
            }
        }
    }
}