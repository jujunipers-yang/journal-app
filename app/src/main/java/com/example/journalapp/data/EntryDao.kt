package com.example.journalapp.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    // Convenience annotations! (automatically implemented by Room)

    // this version of insert will return its id (Long) when called!!
    // -> so photo knows what id to connect to after!
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: Entry): Long

    @Update
    suspend fun update(entry: Entry)

    @Delete
    suspend fun delete(entry: Entry)

    // All other annotations: gotta implement urself!

    @Query("SELECT * from entries WHERE id = :id")
    fun getEntry(id: Int): Flow<Entry>

    @Query("SELECT * from entries ORDER BY date ASC")
    fun getAllEntries(): Flow<List<Entry>>
}