package com.thegeekdogs.repository

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Notes : Table() {
    val id : Column<Int> = integer("note_id").autoIncrement().primaryKey()
    val userId : Column<Int> = integer("user_id").references(Users.userId)
    val noteText = text("note_text")
    val createdDate : Column<String> = varchar("created_date",128)
}