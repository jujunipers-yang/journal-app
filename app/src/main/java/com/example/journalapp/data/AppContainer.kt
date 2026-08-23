package com.example.journalapp.data

import android.content.Context

/**
 * Manual dependency injection container. (?)
 * Creates repositories!
 * Connects database and ViewModels!! (ig bc creates repo?)
 */
interface AppContainer {
    val entryRepository: EntryRepository
    val photoRepository: PhotoRepository
}

/**
 * Implementation of AppContainer interface (above).
 * Creates instance of OfflineRepositories! (via connection to DAO)
 */
class AppDataContainer(private val context: Context) : AppContainer {
    override val entryRepository: EntryRepository by lazy {
        OfflineEntryRepository(JournalDatabase.getDatabase(context).entryDao())
    }
    override val photoRepository: PhotoRepository by lazy {
        OfflinePhotoRepository(JournalDatabase.getDatabase(context).photoDao())
    }
}