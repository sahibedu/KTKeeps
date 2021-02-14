package com.thegeekdogs.repository

import com.thegeekdogs.models.UserModel
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.postgresql.util.PSQLException

class UserRepository : IUserRepository {

    override suspend fun addUser(email: String, displayName: String, passwordHash: String): UserModel? {
        kotlin.runCatching {
            val insertStatement: InsertStatement<Number> = dbQuery {
                Users.insert { user ->
                    user[Users.email] = email
                    user[Users.displayName] = displayName
                    user[Users.passwordHash] = passwordHash
                }
            }
            return UserModel.rowToUser(insertStatement.resultedValues?.get(0))
        }.getOrElse {
            if (it is ExposedSQLException) {
                when (val cause = it.cause) {
                    is PSQLException -> {
                        if (cause.message?.endsWith("already exists.") == true) {
                            throw UserExistsException()
                        } else {
                            throw it
                        }
                    }
                    else -> throw it
                }
            } else throw it
        }
    }

    override suspend fun findUser(userId: Int): UserModel? {
        return dbQuery {
            Users.select(Users.userId.eq(userId)).map { UserModel.rowToUser(it) }.firstOrNull()
        }
    }

    override suspend fun findUserByEmail(email: String): UserModel? {
        return dbQuery {
            Users.select(Users.email.eq(email)).map { UserModel.rowToUser(it) }.firstOrNull()
        }
    }
}

interface IUserRepository {
    suspend fun addUser(email: String, displayName: String, passwordHash: String): UserModel?
    suspend fun findUser(userId: Int): UserModel?
    suspend fun findUserByEmail(email: String): UserModel?
}

class UserExistsException : Throwable()