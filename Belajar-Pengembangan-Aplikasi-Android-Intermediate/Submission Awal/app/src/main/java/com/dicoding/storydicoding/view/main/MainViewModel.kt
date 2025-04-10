package com.dicoding.storydicoding.view.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storydicoding.data.pref.UserModel
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.repository.UserRepository
import com.dicoding.storydicoding.data.response.ListStoryItem
import com.dicoding.storydicoding.di.Injection
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private var storyRepository: StoryRepository
) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun updateStoryRepository(context: Context) {
        storyRepository = Injection.provideStoryRepository(context)
    }

    fun fetchStories() {
        viewModelScope.launch {
            try {
                val stories = storyRepository.getStories()
                _stories.value = stories
            } catch (e: Exception) {
                println("Gagal memuat stories: ${e.message}")
            }
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            userRepository.logout()
            _stories.value = emptyList()
        }
    }
}