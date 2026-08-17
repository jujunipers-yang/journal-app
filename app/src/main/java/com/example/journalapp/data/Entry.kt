package com.example.journalapp.data

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Entity data class - rep a single row in the database. (aka 1 journal entry)
 */
@Entity(tableName = "entries")
data class Entry (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val text: String
)