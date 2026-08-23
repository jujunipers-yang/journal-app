package com.example.journalapp.ui.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.room.util.copy
import android.net.Uri
import com.example.journalapp.data.Entry
import com.example.journalapp.data.EntryRepository
import com.example.journalapp.data.Photo
import com.example.journalapp.data.PhotoRepository

/**
 * Viewmodel - validates + inserts (saves) entries into the db!
 * (connects to repo in order to insert entries)
 */
class NewJournalEntryViewModel(private val entryRepository : EntryRepository, private val photoRepository: PhotoRepository) : ViewModel() {
    // Hold curr entry ui state!! (method of passing entry details to ui screen)
    var entryUiState by mutableStateOf(EntryUiState())
        private set

    // Funcs of viewModel - update ui state (what ui screen sees), validate entry, and save entry!

    /**
     * Update curr ui state (to send to ui screen) with new given entryDetails + validation (NOT for photos!)
     * (called by ui screen whenever input form is changed! - updates entryDetails, ready to be saved at any time)
     */
    fun updateUiState(entryDetails: EntryDetails) {
        entryUiState = entryUiState.copy(
            entryDetails = entryDetails,
            isEntryValid = validateInput(entryDetails)
        )
    }

    /**
     * Update curr ui state with new given photos!
     * (called by ui screen whenever 'Add photo' button is clicked!)
     */
    fun updateUiStatePhotos(uris: List<Uri>) {
        entryUiState = entryUiState.copy(entryPhotoUris = entryUiState.entryPhotoUris + uris) // wanna create whole NEW ui state instance (instead of just changing a value),
        // bc the recomposition (of entryUiState due to mutableStateOf) is only triggered whenever new EntryUiState() obj is created! (recomp is needed
        // to update ui screen w new changed values)
    }

    /**
     * Validates input (checks input taken from ui screen, aka ui state,
     * is not blank!) before saving to repo!
     */
    private fun validateInput(uiState: EntryDetails = entryUiState.entryDetails): Boolean {
        return with(uiState) {
            date.isNotBlank() && text.isNotBlank()
        }
    }

    /**
     * Saves current ui state (wtv it is), as an entry, to repo! (DOESN'T SAVE PHOTOS!)
     * (called by ui screen when 'Save' is pressed!)
     */
    suspend fun saveEntryAndReturnId(): Long {
        var entryId: Long = -1
        if(validateInput()) {
            // insertEntry returns the entry's id!! -> pass back, to use for photo
            entryId = entryRepository.insertEntry(entryUiState.entryDetails.toEntry()) // convert entry ui state to entry
        }
        return entryId
    }

    /**
     * Saves photos of current ui state to repo! Takes entryId of the saved entry!
     * (called by ui screen when 'Save' is pressed!)
     */
    suspend fun savePhotos(entryId: Int) {
        // For each photo uri in ui state entryPhotoUris, save to repo!
        for(photoUri in entryUiState.entryPhotoUris) {
            photoRepository.insertPhoto(photoUri.toPhoto(entryId))
        }
    }

    /**
     * Temporary - until nav host is implemented !!!!!!
     * Reset ui state to blank state (so that when click FAB for new entry,
     * doesn't just take prev entry's ui state, but starts a new one)
     */
    fun resetUiState() {
        entryUiState = EntryUiState()  // back to blank defaults
    }
}



/**
 * Data class containing list of EntryDetails + isEntryValid + all entry's photos!
 */
data class EntryUiState(
    val entryDetails: EntryDetails = EntryDetails(),
    val isEntryValid: Boolean = false,
    val entryPhotoUris: List<Uri> = listOf()
)

/**
 * Data class containing all values in an entry
 * (meant to mirror variables in Entry entity)
 */
data class EntryDetails(
    val id: Int = 0,
    val date: String = "",
    val text: String = ""
)

// IMPLEMENT ALL THE TYPE CONVERSION FUNCS HERE !!!
fun EntryDetails.toEntry(): Entry = Entry(
    id = this.id,
    date = this.date,
    text = this.text
)

fun Entry.toEntryDetails(): EntryDetails = EntryDetails(
    id = this.id,
    date = this.date,
    text = this.text
)

fun Entry.toEntryUiState(isEntryValid: Boolean = false): EntryUiState = EntryUiState(
    entryDetails = this.toEntryDetails(),
    isEntryValid = isEntryValid
)

fun Uri.toPhoto(entryId: Int): Photo = Photo(
    uriString = this.toString(), // this refers to Uri! (class ur converting from)
    entryId = entryId
)