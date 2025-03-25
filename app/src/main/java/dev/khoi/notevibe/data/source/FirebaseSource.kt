package dev.khoi.notevibe.data.source

import com.google.firebase.firestore.FirebaseFirestore
import dev.khoi.notevibe.data.model.NoteData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseSource {
    private val firestore = FirebaseFirestore.getInstance()
    private val usersCollection = firestore.collection("users")
    private val notesCollection = firestore.collection("notes")

    suspend fun addNote(note: NoteData): String {
        val noteWithUserId = note.copy(userId = currentUserId ?: throw IllegalStateException("User not logged in"))
        val document = notesCollection.add(noteWithUserId).await()
        val newId = document.id
        document.set(noteWithUserId.copy(id = newId)).await() // Cập nhật document với id
        return newId
    }

    fun getNotesRealtime(): Flow<List<NoteData>> = callbackFlow {
        val userId = currentUserId ?: run {
            trySend(emptyList()).isSuccess
            close()
            return@callbackFlow
        }

        val listener = notesCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val notes = snapshot?.documents?.map { doc ->
                    NoteData(
                        id = doc.id,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        image = doc.getString("image"),
                        userId = doc.getString("userId") ?: ""
                    )
                } ?: emptyList()
                trySend(notes).isSuccess
            }

        awaitClose { listener.remove() }
    }

    suspend fun updateNote(note: NoteData) {
        if (note.id.isBlank()) throw IllegalArgumentException("Note ID không hợp lệ")
        val noteWithUserId = note.copy(userId = currentUserId ?: throw IllegalStateException("User not logged in"))
        notesCollection.document(note.id).set(noteWithUserId).await()
    }

    suspend fun deleteNote(noteId: String) {
        if (noteId.isBlank()) throw IllegalArgumentException("Note ID không hợp lệ")
        notesCollection.document(noteId).delete().await()
    }

    suspend fun login(userId: String): Boolean {
        if (userId.isBlank()) return false
        val userDoc = usersCollection.document(userId).get().await()
        return if (userDoc.exists()) {
            currentUserId = userId
            true
        } else {
            usersCollection.document(userId).set(mapOf("userId" to userId)).await()
            currentUserId = userId
            true
        }
    }

    fun getCurrentUserId(): String? = currentUserId

    companion object {
        private var currentUserId: String? = null
    }
}