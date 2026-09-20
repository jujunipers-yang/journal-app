package com.example.journalapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.journalapp.ui.entry.EntryEditDestination
import com.example.journalapp.ui.entry.EntryEditScreen
import com.example.journalapp.ui.entry.NewJournalEntryDestination
import com.example.journalapp.ui.entry.NewJournalEntryScreen
import com.example.journalapp.ui.home.HomeDestination
import com.example.journalapp.ui.home.HomeScreen

/**
 * Provides Navigation graph for the application.
 */
@Composable
fun JournalNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToNewJournalEntry = { navController.navigate(NewJournalEntryDestination.route) },
                navigateToEntryEdit = { navController.navigate("${EntryEditDestination.route}/${it}") }
            )
        }
        composable(route = NewJournalEntryDestination.route) {
            NewJournalEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = EntryEditDestination.routeWithArgs,
            arguments = listOf(navArgument(EntryEditDestination.entryIdArg) {
                type = NavType.IntType
            })
        ) {
            EntryEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
