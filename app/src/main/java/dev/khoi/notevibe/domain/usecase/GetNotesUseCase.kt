package dev.khoi.notevibe.domain.usecase

import dev.khoi.notevibe.data.repository.NoteRepository
import dev.khoi.notevibe.domain.model.Note
import kotlinx.coroutines.flow.Flow

class GetNotesUseCase(private val repository: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> {
        return repository.getNotesRealtime()
    }
}