package com.pratclot.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: Int,
    val login: String,
    val avatar_url: String,
    val html_url: String,
    val location: String?,
) : Parcelable
