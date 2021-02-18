package com.thegeekdogs.ktkeeps

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NoteResponseModel(
    val id: Int,
    val noteText: String,
    val createdDate: String
)
