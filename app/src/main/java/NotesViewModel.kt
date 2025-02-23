package com.example.quicksnap

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NotesViewModel : ViewModel() {
    private val _notes = MutableStateFlow<List<String>>(emptyList())
    val notes = _notes.asStateFlow()

    fun addNote(note: String) {
        if (note.isNotBlank()) {
            _notes.value += note
        }
    }

    fun deleteNote(note: String) {
        _notes.value -= note
    }

    fun updateNote(oldNote: String, newNote: String) {
        _notes.value = _notes.value.map { if (it == oldNote) newNote else it }
    }
}