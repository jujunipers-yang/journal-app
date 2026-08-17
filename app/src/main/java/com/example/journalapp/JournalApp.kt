package com.example.journalapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.journalapp.ui.entry.NewJournalEntryScreen
import com.example.journalapp.ui.home.HomeScreen


/**
 * Top-level composable as entry point for  entire application!
 * (called by MainActivity)
 */
@Composable
fun JournalApp() {
    // CODE BELOW IS CURRENTLY PLACEHOLDER FOR NAV GRAPH!!!!!
    // (only doing this now bc only hv 2 screens! once 3+ screens should
    // switch to nav graph...)

    // var to trigger switching to NewJournalEntry screen!
    var showNewJournalEntryScreen by remember { mutableStateOf(false) }

    if(showNewJournalEntryScreen) {
        NewJournalEntryScreen(
            navigateBack = { showNewJournalEntryScreen = false }
        )
    } else {
        HomeScreen(
            navigateToNewJournalEntry = { showNewJournalEntryScreen = true }
        )
    }
}