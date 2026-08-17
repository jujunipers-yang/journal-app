package com.example.journalapp.data

import android.content.Context

/**
 * Manual dependency injection container. (?)
 * Creates entryRepository!
 * Connects database and ViewModels!! (ig bc creates repo?)
 */
interface AppContainer {
    val entryRepository: EntryRepository
}

/**
 * Implementation of AppContainer interface (above).
 * Provides instance of OfflineRepository! (w connection to DAO)
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val entryRepository: EntryRepository by lazy {
        OfflineEntryRepository(JournalDatabase.getDatabase(context).entryDao())
    }
}