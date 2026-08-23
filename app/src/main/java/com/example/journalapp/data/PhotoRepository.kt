package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun insertPhoto(photo: Photo)

    suspend fun updatePhoto(photo: Photo)

    suspend fun deletePhoto(photo: Photo)

    // Get all photos for 1 entry!! (aka same entryId!)
    fun getEntryPhotosStream(entryId: Int): Flow<List<Photo>>
}