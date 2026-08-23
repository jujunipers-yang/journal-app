package com.example.journalapp.data

import kotlinx.coroutines.flow.Flow

/**
 * Implementation of PhotoRepository interface. (wraps the DAO)
 * Basically, gives implementations to all the repo funcs - by connecting w DAO funcs!
 */
class OfflinePhotoRepository(private val photoDao: PhotoDao): PhotoRepository {
    override suspend fun insertPhoto(photo: Photo) = photoDao.insert(photo)

    override suspend fun updatePhoto(photo: Photo) = photoDao.update(photo)

    override suspend fun deletePhoto(photo: Photo) = photoDao.delete(photo)

    // Get all photos for 1 entry!! (aka same entryId!)
    override fun getEntryPhotosStream(entryId: Int): Flow<List<Photo>> = photoDao.getEntryPhotos(entryId)
}