package dev.khoi.notevibe.domain.usecase

import dev.khoi.notevibe.data.repository.NoteRepository
import dev.khoi.notevibe.domain.model.Note

class AddNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note): String {
        if (note.title.isBlank()) throw IllegalArgumentException("title not empty")
        return repository.addNote(note)
    }
}