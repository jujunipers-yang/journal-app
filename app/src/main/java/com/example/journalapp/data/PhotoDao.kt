package com.example.journalapp.data

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    // Convenience annotations! (automatically implemented by Room)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(photo: Photo)

    @Update
    suspend fun update(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)

    // All other annotations: gotta implement urself!

    // Get all photos for 1 entry!! (aka same entryId!)
    @Query("SELECT * from photos WHERE entryId = :entryId")
    fun getEntryPhotos(entryId: Int): Flow<List<Photo>>
}