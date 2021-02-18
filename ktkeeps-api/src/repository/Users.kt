package com.thegeekdogs.repository

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Users : Table() {
    val userId: Column<Int> = integer("user_id").autoIncrement().primaryKey()
    val email: Column<String> = varchar("email", 128).uniqueIndex()
    val displayName: Column<String> = varchar("display_name", 256)
    val passwordHash: Column<String> = varchar("password_hash", 64)
}