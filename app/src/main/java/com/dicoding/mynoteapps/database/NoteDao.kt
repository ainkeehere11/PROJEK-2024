package com.dicoding.mynoteapps.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface NoteDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(note: NoteData)

    @Update
    fun update(note: NoteData)

    @Delete
    fun delete(note: NoteData)

    @Query("SELECT * from notedata ORDER BY id ASC")
    fun getAllNotes(): LiveData<List<NoteData>>

}