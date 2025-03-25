package dev.khoi.notevibe.domain.model

data class Note(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val image: String? = null
)
