package com.dicoding.mybooksapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class books(
    val name: String,
    val description: String,
    val photo: Int,
    val itembooks: String
) : Parcelable {

}
