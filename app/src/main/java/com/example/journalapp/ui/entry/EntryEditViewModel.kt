package com.example.journalapp.ui.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journalapp.MAX_PHOTOS_PER_ENTRY
import com.example.journalapp.data.EntryRepository
import com.example.journalapp.data.PhotoRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EntryEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val entryRepository : EntryRepository,
    private val photoRepository: PhotoRepository) : ViewModel() {

    //
    private val entryId: Int = checkNotNull(savedStateHandle[EntryEditDestination.entryIdArg])


    // Hold curr entry ui state!! (method of passing entry details to ui screen)
    var entryUiState by mutableStateOf(EntryUiState())
        private set


    // init block Kotlin - executes immediately after primary constructor is called
    // aka when EntryEditViewModel instance is created (called from ui screen, aka switching to this screen),
    // get this entry's current uiState to display as existing values on the edit screen!
    /**
     * Load this entry's existing data into the current uiState!
     */
    init {
        viewModelScope.launch {
            // Retrieve entry from repo (contains date/text)
            val entry = entryRepository.getEntryStream(entryId)
                .filterNotNull() // filter: only return values that are not null!
                .first() // bc repo's getItemStream returns a 'stream'/Flow/'list' of Items

            // Retrieve photos from repo
            val photos = photoRepository.getEntryPhotosStream(entryId).first()

            // Load entry + photos into uiState
            entryUiState = entry.toEntryUiState(true).copy( // entry.toEntryUiState - builds base (date, text) to uiState, then create copy to add photoPaths!
                entryPhotoPaths = photos.map { it.path }
            )
        }
    }


    // Funcs of viewModel - update ui state (what ui screen sees), validate entry, and save entry!
    // Only save entry func is diff from NewJournalEntryViewModel! (bc instead of saving a new entry in db,
    // we're retrieving an existing entry and updating it in db)

    /**
     * Update curr ui state (to send to ui screen) with new given entryDetails + validation (NOT for photos!)
     * (called by ui screen whenever input form is changed! - updates entryDetails, ready to be saved at any time)
     * SAME AS NEWJOURNALENTRY
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
     * SAME AS NEWJOURNALENTRY
     */
    fun updateUiStatePhotos(paths: List<String>) {
        entryUiState = entryUiState.copy(entryPhotoPaths = (entryUiState.entryPhotoPaths + paths).take(MAX_PHOTOS_PER_ENTRY)) // wanna create whole NEW ui state instance (instead of just changing a value),
        // bc the recomposition (of entryUiState due to mutableStateOf) is only triggered whenever new EntryUiState() obj is created! (recomp is needed
        // to update ui screen w new changed values)
        // take -> truncates list-adding to max # photos - any photos that go past the limit are ignored!!
    }

    /**
     * Validates input (checks input taken from ui screen, aka ui state,
     * is not blank!) before saving to repo!
     * SAME AS NEWJOURNALENTRY
     */
    private fun validateInput(uiState: EntryDetails = entryUiState.entryDetails): Boolean {
        return with(uiState) {
            date.isNotBlank() && text.isNotBlank()
        }
    }

    /**
     * Updates this entry in repo with its current ui state (wtv it is)!
     * (called by ui screen when 'Save' is pressed!)
     */
    suspend fun updateEntry() {
        if(validateInput()) {
            // UPDATE ENTRY in repo by passing in curr uiState! (func for that in repo!)
            entryRepository.updateEntry(entryUiState.entryDetails.toEntry()) // convert entry ui state to entry

            // UPDATE PHOTOS logic - delete all photos curr in db, then save current
            // Delete existing photos
            val existingPhotos = photoRepository.getEntryPhotosStream(entryId).first()
            for(photo in existingPhotos) {
                photoRepository.deletePhoto(photo)
            }

            // Add back in the curr uiState's photos!
            // For each photo path in ui state entryPhotoPaths, save to repo!
            for(photoPath in entryUiState.entryPhotoPaths) {
                photoRepository.insertPhoto(photoPath.toPhoto(entryId))
            }

        }
    }

    /**
     * Delete this entry
     * (called by ui screen when 'Delete' is pressed)
     */
    suspend fun deleteEntry() {
        // Delete entry data by calling repo
        entryRepository.deleteEntry(entryUiState.entryDetails.toEntry())

        // Delete photos by calling repo
        for(photoPath in entryUiState.entryPhotoPaths) {
            photoRepository.deletePhoto(photoPath.toPhoto(entryId))
        }
    }

}