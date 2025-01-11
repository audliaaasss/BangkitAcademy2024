package com.dicoding.storydicoding.view.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.storydicoding.data.pref.UserModel
import com.dicoding.storydicoding.data.repository.StoryRepository
import com.dicoding.storydicoding.data.repository.UserRepository
import com.dicoding.storydicoding.data.response.ListStoryItem
import com.dicoding.storydicoding.di.Injection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainViewModel(
    private val userRepository: UserRepository,
    private var storyRepository: StoryRepository
) : ViewModel() {

    val stories: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getStories().cachedIn(viewModelScope)

    fun getSession(): LiveData<UserModel> {
        return userRepository.getSession().asLiveData()
    }

    fun updateStoryRepository(context: Context) {
        storyRepository = Injection.provideStoryRepository(context)
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}