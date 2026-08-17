package com.example.journalapp

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import com.example.journalapp.data.Entry
import com.example.journalapp.data.EntryDao
import com.example.journalapp.data.JournalDatabase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException

class EntryDaoTest {
    private lateinit var entryDao: EntryDao // link to DAO! (to test all its funcs)

    private lateinit var journalDatabase: JournalDatabase // JournalDatabase is the one that creates entryDao singleton

    // declare entries for the db to use (as testing example)
    private var entry1 = Entry(1, "2026-01-01", "Happy New Year!")
    private var entry2 = Entry(2, "2026-12-25", "Merry Christmas!")


    @Before // runs before every test
    fun createDb() {
        // Initialize entryDao
        val context: Context = ApplicationProvider.getApplicationContext()
        // Using an in-memory database (aka data does not persist on the disk) bc the information stored here
        // needs to be deleted when the process is killed (bc just testing ig).
        journalDatabase = Room.inMemoryDatabaseBuilder(context, JournalDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        entryDao = journalDatabase.entryDao()
    }

    @After // run after every test - close the db
    @Throws(IOException::class)
    fun closeDb() {
        journalDatabase.close()
    }

    // Utility functions to test! to add entries! (via DAO)
    private suspend fun addOneEntryToDb() {
        entryDao.insert(entry1)
    }

    private suspend fun addTwoEntriesToDb() {
        entryDao.insert(entry1)
        entryDao.insert(entry2)
    }

    // TESTS!!

    // Test utility func - inserting an entry
    @Test
    @Throws(Exception::class)
    fun daoInsert_insertsEntryIntoDB() = runBlocking { // runBlocking - runs test a diff coroutine (that's why use suspend)
        addOneEntryToDb() // add entry to db
        val allEntries = entryDao.getAllEntries().first() // read 1st entry in db
        assertEquals(allEntries[0], entry1) // compare expected value with actual value!
        // bc atp 1st entry in db should be entry1 !
    }

    // Test utility func - inserting 2 entries
    @Test
    @Throws(Exception::class)
    fun daoGetAllEntries_returnsAllEntriesFromDB() = runBlocking {
        addTwoEntriesToDb()
        val allEntries = entryDao.getAllEntries().first()
        assertEquals(allEntries[0], entry1)
        assertEquals(allEntries[1], entry2)
    }

    // Test updating an entity/entry
    @Test
    @Throws(Exception::class)
    fun daoUpdateEntries_updatesEntriesInDB() = runBlocking {
        addTwoEntriesToDb()
        // Update entries
        entryDao.update(Entry(1, "2025-12-31", "Happy New Years' Eve!"))
        entryDao.update(Entry(2, "2026-12-24", "Merry Christmas' Eve!"))

        // Retrieve entities and compare
        val allEntries = entryDao.getAllEntries().first()
        assertEquals(allEntries[0], Entry(1, "2025-12-31", "Happy New Years' Eve!"))
        assertEquals(allEntries[1], Entry(2, "2026-12-24", "Merry Christmas' Eve!"))
    }

    // Test deleting an entity/entry
    @Test
    @Throws(Exception::class)
    fun daoDeleteEntries_deletesAllEntriesFromDB() = runBlocking {
        addTwoEntriesToDb()
        // Delete entries
        entryDao.delete(entry1)
        entryDao.delete(entry2)

        // Retrieve all entries and check that list is empty
        val allEntries = entryDao.getAllEntries().first()
        assertTrue(allEntries.isEmpty())
    }

    // Test getEntry
    @Test
    @Throws(Exception::class)
    fun daoGetEntry_returnsEntriesFromDB() = runBlocking {
        addOneEntryToDb()
        // Retrieve the entry from db
        val entry = entryDao.getEntry(1)
        // Compare retrieved value to actual value (should be entry1)
        assertEquals(entry.first(), entry1)
        // NOTE: need .first(), since entryDao.get funcs return a Flow<Entry> (not just an Entry)
        // so like a 'stream'/'list' of entries, so gotta take the first element
    }

}