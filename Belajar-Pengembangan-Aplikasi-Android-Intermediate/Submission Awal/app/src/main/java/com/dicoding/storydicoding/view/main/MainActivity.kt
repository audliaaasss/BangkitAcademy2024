package com.dicoding.storydicoding.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.storydicoding.R
import com.dicoding.storydicoding.databinding.ActivityMainBinding
import com.dicoding.storydicoding.view.ViewModelFactory
import com.dicoding.storydicoding.view.add.AddStoryActivity
import com.dicoding.storydicoding.view.detail.DetailActivity
import com.dicoding.storydicoding.view.story.StoryAdapter
import com.dicoding.storydicoding.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        viewModel.getSession().observe(this) { user ->
            if (!user.isLogin || user.token.isBlank()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.updateStoryRepository(this)
                viewModel.fetchStories()
            }
        }

        binding.fabAddStory.setOnClickListener {
            val intent = Intent(this, AddStoryActivity::class.java)
            startActivity(intent)
        }

        setupView()
        setupRecyclerView()
        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.option_logout -> {
                showLogoutPopMenu(item)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutPopMenu(item: MenuItem) {
        val popupMenu = PopupMenu(this, findViewById(R.id.option_logout))
        popupMenu.menuInflater.inflate(R.menu.menu_logout, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    viewModel.logout(this)
                    startActivity(Intent(this, WelcomeActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter { storyId ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("STORY_ID", storyId)
            startActivity(intent)
        }
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.stories.observe(this) { stories ->
            if (stories != null && stories.isNotEmpty()) {
                println("Data stories diterima: ${stories.size} items")
            } else {
                println("Tidak ada data stories yang diterima")
            }
            storyAdapter.submitList(stories)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchStories()
    }
}