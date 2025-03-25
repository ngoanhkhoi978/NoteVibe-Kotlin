package dev.khoi.notevibe.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.khoi.notevibe.data.repository.NoteRepositoryImpl
import dev.khoi.notevibe.domain.model.Note
import dev.khoi.notevibe.domain.usecase.AddNoteUseCase
import dev.khoi.notevibe.domain.usecase.DeleteNoteUseCase
import dev.khoi.notevibe.domain.usecase.GetNotesUseCase
import dev.khoi.notevibe.domain.usecase.UpdateNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = NoteRepositoryImpl(application)
    private val getNotesUseCase = GetNotesUseCase(repository)
    private val addNoteUseCase = AddNoteUseCase(repository)
    private val updateNoteUseCase = UpdateNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes: StateFlow<List<Note>> = _notes

    init {
        viewModelScope.launch {
            getNotesUseCase().collect { notes ->
                _notes.value = notes
            }
        }
    }

    suspend fun saveNote(note: Note): String {
        return if (note.id.isBlank()) {
            println("Adding new note: $note")
            val newId = addNoteUseCase(note)
            println("New note added with ID: $newId")
            newId
        } else {
            println("Updating note with ID: ${note.id}")
            updateNoteUseCase(note)
            println("Note updated successfully")
            note.id
        }
    }

    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            deleteNoteUseCase(noteId)
        }
    }
}