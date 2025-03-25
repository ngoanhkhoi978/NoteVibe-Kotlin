package dev.khoi.notevibe.data.repository

import android.content.Context
import android.net.Uri
import dev.khoi.notevibe.data.model.NoteData
import dev.khoi.notevibe.data.source.CloudinarySource
import dev.khoi.notevibe.data.source.FirebaseSource
import dev.khoi.notevibe.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImpl(context: Context) : NoteRepository {
    private val firebaseSource = FirebaseSource()
    private val cloudinarySource = CloudinarySource(context)

    override suspend fun addNote(note: Note): String {
        val imageUrl: String? = note.image?.let { uriString ->
            try {
                val uri = Uri.parse(uriString)
                if (uri.scheme == "content" || uri.scheme == "file") {
                    val uploadedUrl = cloudinarySource.uploadImage(uri)
                    uploadedUrl
                } else if (uriString.startsWith("http")) {
                    uriString
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
        val noteData = NoteData(
            id = "",
            title = note.title,
            description = note.description,
            image = imageUrl
        )
        return firebaseSource.addNote(noteData)
    }

    override fun getNotesRealtime(): Flow<List<Note>> {
        return firebaseSource.getNotesRealtime().map { notesData ->
            notesData.map { it.toDomain() }
        }
    }

    override suspend fun updateNote(note: Note) {
        val imageUrl: String? = note.image?.let { uriString ->
            try {
                println("Processing image URI: $uriString")
                val uri = Uri.parse(uriString)
                if (uri.scheme == "content" || uri.scheme == "file") {
                    val uploadedUrl = cloudinarySource.uploadImage(uri)
                    println("Uploaded image URL: $uploadedUrl")
                    uploadedUrl
                } else if (uriString.startsWith("http")) {
                    uriString
                } else {
                    null
                }
            } catch (e: Exception) {
                println("Error uploading image: ${e.message}")
                e.printStackTrace()
                null
            }
        }
        val noteData = NoteData(
            id = note.id,
            title = note.title,
            description = note.description,
            image = imageUrl,
            userId = firebaseSource.getCurrentUserId() ?: throw IllegalStateException("User not logged in")
        )
        println("Updating note in Firestore: $noteData")
        firebaseSource.updateNote(noteData)
    }

    override suspend fun deleteNote(noteId: String) {
        firebaseSource.deleteNote(noteId)
    }

    private fun NoteData.toDomain() = Note(
        id = id,
        title = title,
        description = description,
        image = image
    )
}