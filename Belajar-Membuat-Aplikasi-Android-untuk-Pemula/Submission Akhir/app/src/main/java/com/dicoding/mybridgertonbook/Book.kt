package com.dicoding.mybridgertonbook
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Book(
    val title: String,
    val synopsys: String,
    val cover: Int,
    val publishedDate: String = "",
    val mainCharacters: String = "",
    val prevBook: String = "",
    val nextBook: String = ""
) : Parcelable
