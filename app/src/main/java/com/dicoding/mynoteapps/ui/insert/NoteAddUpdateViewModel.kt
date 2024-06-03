package com.dicoding.mynoteapps.ui.insert

import android.app.Application
import androidx.lifecycle.ViewModel
import com.dicoding.mynoteapps.database.NoteData
import com.dicoding.mynoteapps.repository.NoteRepository

class NoteAddUpdateViewModel(application: Application) : ViewModel() {
    private val mNoteRepository: NoteRepository = NoteRepository(application)

    fun insert(note: NoteData) {
        mNoteRepository.insert(note)
    }

    fun update(note: NoteData) {
        mNoteRepository.update(note)
    }

    fun delete(note: NoteData) {
        mNoteRepository.delete(note)
    }
}

//ini untuk hubungkan repository dan activity//