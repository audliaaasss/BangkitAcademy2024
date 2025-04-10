package com.dicoding.eventdicoding.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.eventdicoding.data.database.FavoriteEvent
import com.dicoding.eventdicoding.data.repository.FavoriteEventRepository
import kotlinx.coroutines.launch

class FavoriteEventViewModel(private val repository: FavoriteEventRepository) : ViewModel() {
    val favoriteEvents: LiveData<List<FavoriteEvent>> = repository.getFavoriteEvents() // Expose LiveData
}