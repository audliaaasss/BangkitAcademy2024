package com.dicoding.storydicoding.view.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.response.ListStoryItem
import kotlinx.coroutines.launch

class MapsViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocation: LiveData<List<ListStoryItem>> = _storiesWithLocation

    fun fetchStoriesWithLocation() {
        viewModelScope.launch {
            try {
                val stories = storyRepository.getStoriesWithLocation()
                _storiesWithLocation.postValue(stories)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}