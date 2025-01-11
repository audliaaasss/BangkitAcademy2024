package com.dicoding.storydicoding.view.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun getDetailStory(storyId: String): LiveData<Story> {
        val detailStory = MutableLiveData<Story>()
        viewModelScope.launch {
            try {
                val response = storyRepository.getStoryDetail(storyId)
                response.story?.let { detailStory.postValue(it) }
            } catch (e: Exception) {
                println("Error: ${e.message}")
            }
        }
        return detailStory
    }
}