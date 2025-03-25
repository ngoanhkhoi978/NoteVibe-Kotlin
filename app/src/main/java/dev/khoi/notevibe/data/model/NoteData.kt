package dev.khoi.notevibe.data.model

data class NoteData(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val image: String? = null,
    val userId: String = ""
)
