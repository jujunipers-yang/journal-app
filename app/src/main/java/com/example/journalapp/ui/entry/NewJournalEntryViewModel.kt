package com.example.journalapp.ui.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.journalapp.data.Entry
import com.example.journalapp.data.EntryRepository

/**
 * Viewmodel - validates + inserts (saves) entries into the db!
 * (connects to repo in order to insert entries)
 */
class NewJournalEntryViewModel(private val entryRepository : EntryRepository) : ViewModel() {
    // Hold curr entry ui state!! (method of passing entry details to ui screen)
    var entryUiState by mutableStateOf(EntryUiState())
        private set

    // Funcs of viewModel - update ui state (what ui screen sees), validate entry, and save entry!

    /**
     * Update curr ui state (to send to ui screen) with new given entryDetails + validation
     * (called by ui screen whenever input form is changed! - updates entryDetails, ready to be saved at any time)
     */
    fun updateUiState(entryDetails: EntryDetails) {
        entryUiState = EntryUiState(entryDetails = entryDetails, isEntryValid = validateInput(entryDetails))
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
     * Saves current ui state (wtv it is), as an entry, to repo!
     * (called by ui screen when 'Save' is pressed!)
     */
    suspend fun saveItem() {
        if(validateInput()) {
            entryRepository.insertEntry(entryUiState.entryDetails.toEntry()) // convert entry ui state to entry
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
 * Data class containing list of EntryDetails + isEntryValid
 */
data class EntryUiState(
    val entryDetails: EntryDetails = EntryDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Data class containing all values in an entry
 */
data class EntryDetails(
    val id: Int = 0,
    val date: String = "",
    val text: String = ""
)

// IMPLEMENT ALL THE TYPE CONVERSION FUNCS HERE !!!
fun EntryDetails.toEntry(): Entry = Entry(
    id = id,
    date = date,
    text = text
)

fun Entry.toEntryDetails(): EntryDetails = EntryDetails(
    id = id,
    date = date,
    text = text
)

fun Entry.toEntryUiState(isEntryValid: Boolean = false): EntryUiState = EntryUiState(
    entryDetails = this.toEntryDetails(),
    isEntryValid = isEntryValid
)