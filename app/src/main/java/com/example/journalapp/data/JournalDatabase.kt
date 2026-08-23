package com.example.journalapp.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase

/**
 * Database class with a singleton Instance object.
 * connects to entryDao!
 *
 * abstract bc RoomDatabase (parent) builds implementation for u!
 * (so ur just providing the functions that u want it to include)
 */
@Database(entities = [Entry::class, Photo::class], version = 1, exportSchema = false)
abstract class JournalDatabase : RoomDatabase() {

    // singleton - connection to dao!
    abstract fun entryDao(): EntryDao
    abstract fun photoDao(): PhotoDao

    // stores static variables/functions in kotlin!
    companion object {

        // instantiate singleton variable for this database class
        @Volatile
        private var Instance: JournalDatabase? = null

        // singleton for other classes to access this db
        fun getDatabase(context: Context): JournalDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, JournalDatabase::class.java, "journal_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }

}