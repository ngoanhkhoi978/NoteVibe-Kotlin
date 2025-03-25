package dev.khoi.notevibe.domain.usecase

import dev.khoi.notevibe.data.repository.NoteRepository

class DeleteNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(noteId: String) {
        if (noteId.isBlank()) throw IllegalArgumentException("ID không hợp lệ!")
        repository.deleteNote(noteId)
    }
}