package com.dicoding.mynoteapps.helper

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.mynoteapps.database.NoteData

class NoteDiffCallback(private val oldNoteList: List<NoteData>, private val newNoteList: List<NoteData>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldNoteList.size
    override fun getNewListSize(): Int = newNoteList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldNoteList[oldItemPosition].id == newNoteList[newItemPosition].id
    }
    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldNoteList[oldItemPosition]
        val newNote = newNoteList[newItemPosition]
        return oldNote.title == newNote.title && oldNote.description == newNote.description
    }
}


//untuk melakukan pengecekan apakah ada perubahan list note. Kelas ini nanti akan dipanggil di kelas adapter