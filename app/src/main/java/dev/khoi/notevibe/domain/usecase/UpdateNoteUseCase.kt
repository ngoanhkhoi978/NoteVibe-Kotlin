package dev.khoi.notevibe.domain.usecase

import dev.khoi.notevibe.data.repository.NoteRepository
import dev.khoi.notevibe.domain.model.Note

class UpdateNoteUseCase(private val repository: NoteRepository) {
    suspend operator fun invoke(note: Note) {
        if (note.title.isBlank()) throw IllegalArgumentException("Tiêu đề không được trống!")
        if (note.id.isBlank()) throw IllegalArgumentException("ID không hợp lệ!")
        repository.updateNote(note)
    }
}