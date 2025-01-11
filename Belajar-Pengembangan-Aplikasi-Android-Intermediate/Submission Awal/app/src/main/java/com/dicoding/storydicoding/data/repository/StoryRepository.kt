package com.dicoding.storydicoding.data.repository

import com.dicoding.storydicoding.data.api.StoryService
import com.dicoding.storydicoding.data.response.AddStoryResponse
import com.dicoding.storydicoding.data.response.DetailResponse
import com.dicoding.storydicoding.data.response.ListStoryItem
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val storyService: StoryService) {
    suspend fun getStories(): List<ListStoryItem> {
        val response = storyService.getStories()
        return response.listStory
    }

    suspend fun getStoryDetail(storyId: String): DetailResponse {
        val response = storyService.getStoryDetail(storyId)
        return response
    }

    suspend fun uploadStories(file: MultipartBody.Part, description: RequestBody): AddStoryResponse {
        val response = storyService.uploadStories(file, description)
        return response
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(storyService: StoryService): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyService)
            }.also { instance = it }
    }
}