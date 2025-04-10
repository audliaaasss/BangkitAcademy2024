package com.dicoding.eventdicoding.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoriteEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteEvent: FavoriteEvent)

    @Query("SELECT * FROM favorite_events")
    fun getAllFavorites(): LiveData<List<FavoriteEvent>>

    @Query("SELECT * FROM favorite_events WHERE id = :id")
    suspend fun getFavoriteEventById(id: String): FavoriteEvent?

    @Query("DELETE FROM favorite_events WHERE id = :eventId")
    suspend fun deleteById(eventId: String)

    @Delete
    suspend fun delete(event: FavoriteEvent)
}