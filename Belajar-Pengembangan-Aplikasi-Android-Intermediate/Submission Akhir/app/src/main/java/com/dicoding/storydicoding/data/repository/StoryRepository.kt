package com.dicoding.storydicoding.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storydicoding.data.api.StoryService
import com.dicoding.storydicoding.data.paging.StoryPagingSource
import com.dicoding.storydicoding.data.response.AddStoryResponse
import com.dicoding.storydicoding.data.response.DetailResponse
import com.dicoding.storydicoding.data.response.ListStoryItem
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryRepository(private val storyService: StoryService) {
    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StoryPagingSource(storyService)
            }
        ).liveData
    }

    suspend fun getStoryDetail(storyId: String): DetailResponse {
        val response = storyService.getStoryDetail(storyId)
        return response
    }

    suspend fun uploadStories(file: MultipartBody.Part, description: RequestBody): AddStoryResponse {
        val response = storyService.uploadStories(file, description)
        return response
    }

    suspend fun getStoriesWithLocation(): List<ListStoryItem> {
        val response = storyService.getStoriesWithLocation()
        return response.listStory
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