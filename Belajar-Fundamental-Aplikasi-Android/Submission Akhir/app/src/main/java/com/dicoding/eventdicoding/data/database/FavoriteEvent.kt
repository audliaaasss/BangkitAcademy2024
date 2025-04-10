package com.dicoding.eventdicoding.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_events")
data class FavoriteEvent(
    @PrimaryKey(autoGenerate = false)
    var id: String = "",
    var name: String = "",
    var mediaCover: String? = null,
)