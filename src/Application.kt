package com.thegeekdogs

import com.thegeekdogs.auth.JwtService
import com.thegeekdogs.auth.MySession
import com.thegeekdogs.repository.DatabaseFactory
import com.thegeekdogs.repository.UserRepository
import com.thegeekdogs.route.createUser
import io.ktor.application.*
import io.ktor.locations.*
import io.ktor.sessions.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.routing.*

const val API_VERSION = "/v1"


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@KtorExperimentalLocationsAPI
@Suppress("unused") // Referenced in application.conf
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
    val db = UserRepository()
    val jwtService = JwtService()

    install(Authentication) {
        jwt("jwt") {
            verifier(jwtService.verifier)
            realm = "KTKeeps Server"
            validate {
                val payload = it.payload
                val claim = payload.getClaim("id").asInt()
                db.findUser(claim)
            }
        }
    }

    install(ContentNegotiation) {
        gson {
        }
    }

    routing {
        createUser(db, jwtService)
    }
}


