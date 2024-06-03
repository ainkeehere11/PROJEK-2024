package com.dicoding.asclepius.riwayat

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_predict")
data class HistoryPrediction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imagePath: String,
    val result: String,
)
