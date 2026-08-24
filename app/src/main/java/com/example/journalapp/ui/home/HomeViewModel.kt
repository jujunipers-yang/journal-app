package com.example.journalapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journalapp.data.Entry
import com.example.journalapp.data.EntryRepository
import com.example.journalapp.data.PhotoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel - retrieves entries from db!
 * (connects to repo! to get these entries)
 */
class HomeViewModel(entryRepository: EntryRepository, private val photoRepository: PhotoRepository) : ViewModel() {

    // variable containing list of all entries!! (as type StateFlow)
    // (StateFlow - observable API for UI state, which enables the UI to update itself from changes made to db automatically)
    val homeUiState: StateFlow<HomeUiState> =
        entryRepository.getAllEntriesStream().map { HomeUiState(it) } // stores list of entries (from repo) into HomeUiState data class!
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
                initialValue = HomeUiState()
            )


    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    /**
     * Gets all photos from one entry, given its entryId
     * (connects to repo, just wraps its func!)
     */
    fun getEntryPhotosStream(entryId: Int) = photoRepository.getEntryPhotosStream(entryId)
}

/**
 * UI state for HomeScreen - contains list of all Entries to display!
 * (retrieves list of entries from repo!)
 */
data class HomeUiState(
    val entriesList: List<Entry> = listOf()
)