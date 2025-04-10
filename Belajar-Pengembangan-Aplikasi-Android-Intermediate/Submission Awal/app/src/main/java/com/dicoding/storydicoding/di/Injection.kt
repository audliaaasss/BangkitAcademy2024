package com.dicoding.storydicoding.di

import android.content.Context
import com.dicoding.storydicoding.data.api.ApiConfig
import com.dicoding.storydicoding.data.api.StoryService
import com.dicoding.storydicoding.data.pref.UserPreference
import com.dicoding.storydicoding.data.pref.dataStore
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val authService = ApiConfig.getAuthApiService()
        return UserRepository.getInstance(authService, pref)
    }
    fun provideStoryRepository(context: Context): StoryRepository {
        val storyService = ApiConfig.getStoryApiService(context)
        return StoryRepository.getInstance(storyService)
    }
}