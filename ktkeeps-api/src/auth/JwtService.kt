package com.thegeekdogs.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.thegeekdogs.models.UserModel
import java.util.*

class JwtService {

    companion object {
        private const val ISSUER = "TheGeekDogs_KtKeeps"
    }

    private val jwtSecret = System.getenv("JWT_SECRET")
    private val algorithm = Algorithm.HMAC512(jwtSecret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(ISSUER)
        .build()

    fun generateToken(userModel: UserModel): String {
        return JWT
            .create()
            .withSubject("Authentication")
            .withIssuer(ISSUER)
            .withClaim("id", userModel.userId)
            .withExpiresAt(Date(System.currentTimeMillis() + 3_600_000 * 24)) // 24 hours
            .sign(algorithm)
    }
}