package com.example.journalapp.data

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.PrimaryKey
import coil3.Uri

/**
 * Entity data class just for photos - 1 row/item = 1 photo!
 * Each photo connects to 1 entry via entryId variable (multiple photos
 * can be connected to the same entry!) -> ForeignKey (multiple-to-one pattern)
 */
@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = Entry::class,          // Parent entity class
            parentColumns = ["id"],         // Column in parent entity to look at
            childColumns = ["entryId"],     // Column in child entity that should match up w parent column
            onDelete = ForeignKey.CASCADE   // Delete photos if entity is deleted
        )
    ]
)
data class Photo (
    @PrimaryKey(autoGenerate = true) val photoId: Int = 0,
    val path: String,
    val entryId: Int
)