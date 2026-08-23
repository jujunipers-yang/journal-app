package com.example.journalapp.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.journalapp.JournalApplication
import com.example.journalapp.ui.entry.NewJournalEntryViewModel
import com.example.journalapp.ui.home.HomeViewModel

/**
 * Provides Factory to create instance of ViewModel for entire app.
 * (and connects them with repos in their param)
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(journalApplication().container.entryRepository)
        }

        // Initializer for NewJournalEntryViewModel
        initializer {
            NewJournalEntryViewModel(journalApplication().container.entryRepository, journalApplication().container.photoRepository)
        }
    }
}

/**
 * Extension function to queries for Application object and returns an instance of
 * JournalApplication.
 */
fun CreationExtras.journalApplication(): JournalApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as JournalApplication)