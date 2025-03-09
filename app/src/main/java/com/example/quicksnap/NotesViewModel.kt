package com.example.quicksnap

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val noteDao = database.noteDao()

    val notes = noteDao.getAllNotes()

    fun addNote(content: String) {
        viewModelScope.launch {
            noteDao.insert(Note(content = content))
        }
    }

    fun updateNote(note: Note, newContent: String) {
        viewModelScope.launch {
            val updatedNote = note.copy(content = newContent)
            noteDao.update(updatedNote)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            noteDao.delete(note)
        }
    }
}
