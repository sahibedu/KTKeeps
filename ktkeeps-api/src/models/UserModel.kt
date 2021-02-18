package com.thegeekdogs.models

import com.thegeekdogs.repository.Users
import io.ktor.auth.*
import org.jetbrains.exposed.sql.ResultRow
import java.io.Serializable

data class UserModel(
    val userId: Int,
    val email: String,
    val displayName: String,
    val passwordHash: String
) : Serializable, Principal {

    companion object {
        fun rowToUser(row: ResultRow?): UserModel? {
            if (row == null) {
                return null
            }
            return UserModel(
                userId = row[Users.userId],
                email = row[Users.email],
                displayName = row[Users.displayName],
                passwordHash = row[Users.passwordHash]
            )
        }
    }
}