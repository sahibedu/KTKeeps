package com.thegeekdogs.repository

import com.thegeekdogs.models.NoteModel
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.time.LocalDate

class NotesRepository : INotesRepository {
    override suspend fun addNote(userId: Int, noteText: String, noteCreatedDate: LocalDate): NoteModel? {
        val insertStatement = dbQuery {
            Notes.insert { note ->
                note[Notes.userId] = userId
                note[Notes.noteText] = noteText
                note[createdDate] = noteCreatedDate.toString()
            }
        }
        return NoteModel.rowToNote(insertStatement.resultedValues?.firstOrNull())
    }

    override suspend fun getNotes(userId: Int): List<NoteModel> {
        return dbQuery {
            Notes.select(Notes.userId.eq(userId)).mapNotNull { NoteModel.rowToNote(it) }
        }
    }

    override suspend fun deleteNote(userId: Int, noteId: Int): Int {
        return dbQuery {
            return@dbQuery Notes.deleteWhere {
                Notes.id.eq(noteId).also { Notes.userId.eq(userId) }
            }
        }
    }
}

interface INotesRepository {
    suspend fun addNote(userId: Int, noteText: String, noteCreatedDate: LocalDate): NoteModel?
    suspend fun getNotes(userId: Int): List<NoteModel>
    suspend fun deleteNote(userId: Int, noteId: Int): Int
}