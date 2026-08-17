package com.example.journalapp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.journalapp.ui.navigation.NavigationDestination
import com.example.journalapp.R
import com.example.journalapp.data.Entry
import com.example.journalapp.ui.AppViewModelProvider
import com.example.journalapp.ui.theme.JournalAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add

object HomeDestination : NavigationDestination {
    override val route = "home"

    override val titleRes = R.string.app_name
}


/**
 * Entry route for Home Screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToNewJournalEntry: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory) // connection to viewModel!!! (to retrieve entries)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // collect UI state from viewModel!! (aka list of items variable)
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

        // FAB TO CREATE NEW JOURNAL ENTRY!
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToNewJournalEntry
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_new_entry_title)
                )
            }
        },
    ) { innerPadding ->
            // Display List header and List of Entries
        HomeBody(
            entriesList = homeUiState.entriesList, // PASS IN LIST OF ENTRIES!!!!
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

@Composable
private fun HomeBody(
    entriesList: List<Entry>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    Column(
        modifier = modifier,
    ) {
        if(entriesList.isEmpty()) {
            Text(
                text = stringResource(R.string.no_entry_description),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(contentPadding),
            )
        } else {
            // DISPLAY LIST OF JOURNAL ENTRIES
            JournalEntriesList(
                entriesList,
                modifier = Modifier.padding()
            )
        }
    }
}

@Composable
private fun JournalEntriesList(
    entriesList: List<Entry>,
    modifier: Modifier = Modifier
) {
    // LIST OF ALL THE JOURNALENTRY COMPOSABLES!
    LazyColumn( // high-performance vertical scrolling list component, gud for displaying large/dynamic lists
        modifier = modifier,
    ) {
        items(items = entriesList, key = { it.id }) { entry ->
            JournalEntry(entry = entry,
                modifier = Modifier
                )
        }
    }


}

@Composable
private fun JournalEntry(
    entry: Entry, modifier: Modifier = Modifier

) {
    Column(
        modifier,
    ) {
        Text(
            text = entry.date,
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = entry.text,
            style = MaterialTheme.typography.titleMedium
        )
    }

}



@Preview(showBackground = true)
@Composable
fun HomeBodyPreview() {
    JournalAppTheme {
        HomeBody(
            listOf(
                Entry(1, "2026-01-12", "Hello my name is Julia."),
                Entry(2, "2026-02-21", "Hello my name is Kate."),
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    JournalAppTheme {
        HomeBody(
            listOf()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JournalEntryPreview() {
    JournalAppTheme {
        JournalEntry(
            Entry(1, "2026-01-12", "Hello my name is Julia.")
        )
    }
}