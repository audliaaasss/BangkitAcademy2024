package com.dicoding.eventdicoding.data.repository

import androidx.lifecycle.LiveData
import com.dicoding.eventdicoding.data.database.FavoriteEvent
import com.dicoding.eventdicoding.data.database.FavoriteEventDao

class FavoriteEventRepository(private val favoriteEventDao: FavoriteEventDao) {
    fun getFavoriteEvents(): LiveData<List<FavoriteEvent>> {
        return favoriteEventDao.getAllFavorites()
    }
}