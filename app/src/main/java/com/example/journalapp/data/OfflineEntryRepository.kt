package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow

/**
 * Implementation of EntryRepository interface. (wraps the DAO)
 * Basically, gives implementations to all the repo funcs - by connecting w DAO funcs!
 */
class OfflineEntryRepository(private val entryDao: EntryDao) : EntryRepository {
    override fun getAllEntriesStream(): Flow<List<Entry>> = entryDao.getAllEntries()

    override fun getEntryStream(id: Int): Flow<Entry?> = entryDao.getEntry(id)

    override suspend fun insertEntry(entry: Entry) = entryDao.insert(entry)

    override suspend fun deleteEntry(entry: Entry) = entryDao.delete(entry)

    override suspend fun updateEntry(entry: Entry) = entryDao.update(entry)
}