package com.thegeekdogs.models

data class NoteModel(
    val id: Int,
    val userId: Int,
    val noteText: String,
    val createdDate:Long
)
