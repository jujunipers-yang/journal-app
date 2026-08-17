package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow

interface EntryRepository {

    suspend fun insertEntry(entry: Entry)

    suspend fun updateEntry(entry: Entry)

    suspend fun deleteEntry(entry: Entry)

    fun getEntryStream(id: Int): Flow<Entry?>

    fun getAllEntriesStream(): Flow<List<Entry>>
}