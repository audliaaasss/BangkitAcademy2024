package com.dicoding.storydicoding.view.add

import android.media.session.MediaSession.Token
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.response.AddStoryResponse
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddStoryViewModel(private val storyRepository: StoryRepository) : ViewModel() {

    fun uploadStory(
        file: MultipartBody.Part,
        description: RequestBody,
        onSuccess: (AddStoryResponse) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = storyRepository.uploadStories(file, description)
                onSuccess(response)
            } catch (e: Exception) {
                onError(e)
            }
        }
    }
}