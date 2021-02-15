package com.thegeekdogs

import com.thegeekdogs.auth.JwtService
import com.thegeekdogs.auth.MySession
import com.thegeekdogs.repository.DatabaseFactory
import com.thegeekdogs.repository.NotesRepository
import com.thegeekdogs.repository.UserRepository
import com.thegeekdogs.route.createNote
import com.thegeekdogs.route.createUser
import com.thegeekdogs.route.loginUser
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*
import kotlin.collections.set

const val API_VERSION = "/v1"


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(Locations) {
    }

    install(Sessions) {
        cookie<MySession>("MY_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }

    DatabaseFactory.init()
    val userRepository = UserRepository()
    val notesRepository = NotesRepository()
    val jwtService = JwtService()

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "KTKeeps Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id").asInt()
                userRepository.findUser(claim)
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        get("/") {
            call.respondText("Welcome To KTKeeps API Homepage", contentType = ContentType.Text.Plain)
        }
        createUser(userRepository, jwtService)
        loginUser(userRepository, jwtService)
        createNote(userRepository, notesRepository)
    }
}


