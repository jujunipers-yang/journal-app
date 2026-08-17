package com.example.journalapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.journalapp.data.Entry
import com.example.journalapp.data.EntryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel - retrieves entries from db!
 * (connects to repo! to get these entries)
 */
class HomeViewModel(entryRepository: EntryRepository) : ViewModel() {

    // variable containing list of all entries!! (as type StateFlow)
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
}

/**
 * UI state for HomeScreen - contains list of all Entries to display!
 * (retrieves list of entries from repo!)
 */
data class HomeUiState(val entriesList: List<Entry> = listOf())