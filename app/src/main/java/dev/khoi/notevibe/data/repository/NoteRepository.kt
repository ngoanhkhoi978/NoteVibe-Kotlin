package dev.khoi.notevibe.data.repository

import dev.khoi.notevibe.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getNotesRealtime(): Flow<List<Note>>
    suspend fun addNote(note: Note): String
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(noteId: String)
}