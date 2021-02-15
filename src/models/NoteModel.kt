package com.thegeekdogs.models

import com.thegeekdogs.repository.Notes
import org.jetbrains.exposed.sql.ResultRow
import java.time.LocalDate

data class NoteModel(
    val id: Int,
    val userId: Int,
    val noteText: String,
    val createdDate: LocalDate
) {
    companion object {
        fun rowToNote(row: ResultRow?): NoteModel? {
            if (row == null) {
                return null
            }
            return NoteModel(
                id = row[Notes.id],
                userId = row[Notes.userId],
                noteText = row[Notes.noteText],
                createdDate = LocalDate.parse(row[Notes.createdDate])
            )
        }
    }
}
