package com.thegeekdogs.route

import com.thegeekdogs.API_VERSION
import com.thegeekdogs.auth.MySession
import com.thegeekdogs.repository.NotesRepository
import com.thegeekdogs.repository.UserRepository
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import java.time.LocalDate

private const val NOTES = "$API_VERSION/notes"

@KtorExperimentalLocationsAPI
@Location(NOTES)
class NoteCommonRoute

object NotesRouteApiContract {
    const val PARAM_NOTE_TEXT = "note_text"
    const val PARAM_NOTE_DATE = "note_created_date"
    const val PARAM_NOTE_ID = "note_id"
}

@KtorExperimentalLocationsAPI
fun Route.createNote(userRepository: UserRepository, notesRepository: NotesRepository) {
    authenticate("jwt") {
        post<NoteCommonRoute> {
            val callParams = call.receiveParameters()
            val noteText = callParams[NotesRouteApiContract.PARAM_NOTE_TEXT]
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
            val noteDateString = callParams[NotesRouteApiContract.PARAM_NOTE_DATE]
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")

            kotlin.runCatching {
                val noteDate = LocalDate.parse(noteDateString)
                val user = call.sessions.get<MySession>()?.let {
                    userRepository.findUser(it.userId)
                }
                if (user != null) {
                    val noteInserted = notesRepository.addNote(user.userId, noteText, noteDate)
                    if (noteInserted != null) {
                        return@post call.respond(HttpStatusCode.OK, noteInserted)
                    } else {
                        return@post call.respond(HttpStatusCode.InternalServerError, "Note Not Inserted")
                    }
                } else {
                    return@post call.respond(HttpStatusCode.InternalServerError, "User Not Found")
                }
            }.getOrElse {
                application.log.error("$NOTES POST Failed", it)
                return@post call.respond(HttpStatusCode.InternalServerError)
            }
        }

        get<NoteCommonRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            if (user == null) {
                call.respond(HttpStatusCode.InternalServerError, "User Not Found")
                return@get
            }
            kotlin.runCatching {
                val notesList = notesRepository.getNotes(user.userId)
                call.respond(notesList)
            }.getOrElse {
                application.log.error("$NOTES GET Failed", it)
                return@get call.respond(HttpStatusCode.InternalServerError)
            }
        }

        delete<NoteCommonRoute> {
            val user = call.sessions.get<MySession>()?.let { userRepository.findUser(it.userId) }
            val callParams = call.receiveParameters()
            val noteId = callParams[NotesRouteApiContract.PARAM_NOTE_ID]
                ?: return@delete call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
            if (user == null) {
                call.respond(HttpStatusCode.InternalServerError, "User Not Found")
                return@delete
            }
            kotlin.runCatching {
                val notesDeletedCount = notesRepository.deleteNote(user.userId, noteId.toInt())
                if (notesDeletedCount > 0) {
                    call.respond(HttpStatusCode.OK)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Note Not Deleted")
                }
            }.getOrElse {
                application.log.error("$NOTES DELETE Failed", it)
                return@delete call.respond(HttpStatusCode.InternalServerError, it)
            }
        }
    }
}