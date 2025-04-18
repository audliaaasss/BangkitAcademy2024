package com.dicoding.mynotesapp.entity

import android.os.Parcelable
import android.provider.ContactsContract.Data
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    var id: Int = 0,
    var title: String? = null,
    var description: String? = null,
    var date: String? = null
) : Parcelable
