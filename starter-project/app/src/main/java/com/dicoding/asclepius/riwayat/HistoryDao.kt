package com.dicoding.asclepius.riwayat

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_predict")
    suspend fun getAllPredictions(): List<HistoryPrediction>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrediction(prediction: HistoryPrediction)

    @Delete
    suspend fun deletePrediction(prediction: HistoryPrediction)
}