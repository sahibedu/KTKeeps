package com.thegeekdogs.route

import com.thegeekdogs.API_VERSION
import com.thegeekdogs.auth.JwtService
import com.thegeekdogs.auth.MySession
import com.thegeekdogs.auth.toHash
import com.thegeekdogs.repository.UserExistsException
import com.thegeekdogs.repository.UserRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.sessions.*

private const val USERS = "$API_VERSION/users"
private const val USER_LOGIN = "$USERS/login"
private const val USER_CREATE = "$USERS/create"

@KtorExperimentalLocationsAPI
@Location(USER_LOGIN)
class UserLoginRoute

@KtorExperimentalLocationsAPI
@Location(USER_CREATE)
class UserCreateRoute

object UserRouteApiContract {
    const val PARAM_EMAIL = "email"
    const val PARAM_PASSWORD = "password"
    const val PARAM_DISPLAY_NAME = "displayName"
}

@KtorExperimentalLocationsAPI
fun Route.createUser(db: UserRepository, jwtService: JwtService) {
    post<UserCreateRoute> {
        val signUpParams = call.receiveParameters()
        val password =
            signUpParams[UserRouteApiContract.PARAM_PASSWORD] ?: return@post call.respond(
                HttpStatusCode.Unauthorized,
                "Missing Fields"
            )
        val displayName =
            signUpParams[UserRouteApiContract.PARAM_DISPLAY_NAME]
                ?: return@post call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
        val emailAddress =
            signUpParams[UserRouteApiContract.PARAM_EMAIL] ?: return@post call.respond(
                HttpStatusCode.Unauthorized,
                "Missing Fields"
            )
        kotlin.runCatching {
            val newUser = db.addUser(emailAddress, displayName, password.toHash())
            if (newUser?.userId != null) {
                call.sessions.set(MySession(newUser.userId))
                call.respondText(
                    jwtService.generateToken(newUser),
                    status = HttpStatusCode.Created
                )
            } else {
                call.respond(HttpStatusCode.Unauthorized, "Missing Fields")
            }
        }.getOrElse {
            application.log.error("$USER_CREATE Failed", it)
            if (it is UserExistsException) {
                val userModel = db.findUserByEmail(emailAddress)
                if (userModel?.userId != null) {
                    call.sessions.set(MySession(userModel.userId))
                    call.respondText(
                        jwtService.generateToken(userModel),
                        status = HttpStatusCode.Accepted
                    )
                } else {
                    call.respond(HttpStatusCode.InternalServerError)
                }
            } else {
                call.respond(HttpStatusCode.InternalServerError)
            }
        }
    }
}

@KtorExperimentalLocationsAPI
fun Route.loginUser(db: UserRepository, jwtService: JwtService) {
    post<UserLoginRoute> {
        val signInParams = call.receiveParameters()
        val emailAddress = signInParams[UserRouteApiContract.PARAM_EMAIL] ?: return@post call.respond(
            HttpStatusCode.Unauthorized,
            "Missing Fields"
        )
        val password = signInParams[UserRouteApiContract.PARAM_PASSWORD] ?: return@post call.respond(
            HttpStatusCode.Unauthorized,
            "Missing Fields"
        )
        kotlin.runCatching {
            val userModel = db.findUserByEmail(emailAddress)
            if (userModel != null) {
                // User Exists
                if (userModel.passwordHash == password.toHash()) {
                    // Password Match
                    call.sessions.set(MySession(userModel.userId))
                    call.respondText(
                        jwtService.generateToken(userModel),
                        status = HttpStatusCode.Accepted
                    )
                } else {
                    call.respond(HttpStatusCode.BadRequest, "Password not matched")
                }
            } else {
                call.respond(HttpStatusCode.NotFound, "User Not Found")
            }
        }.getOrElse {
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}