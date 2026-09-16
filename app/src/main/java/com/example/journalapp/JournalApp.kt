package com.example.journalapp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.journalapp.ui.entry.NewJournalEntryScreen
import com.example.journalapp.ui.home.HomeScreen
import com.example.journalapp.ui.navigation.JournalNavHost


/**
 * Top-level composable as entry point for entire application!
 * (called by MainActivity)
 */
@Composable
fun JournalApp(navController: NavHostController = rememberNavController()) {
    JournalNavHost(navController = navController)
}