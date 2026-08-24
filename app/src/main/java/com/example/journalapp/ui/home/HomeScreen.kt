package com.example.journalapp.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.example.journalapp.data.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.io.File

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
    val homeUiState by viewModel.homeUiState.collectAsState() // collectAsState - converts homeUiState's StateFlow to State, triggers ui update/recomp when new data arrives

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
            getEntryPhotosStream = viewModel::getEntryPhotosStream,
            modifier = modifier.fillMaxSize(),
            contentPadding = innerPadding
        )
    }
}

// BODY - EITHER EMPTY OR DISPLAYS JOURNALENTRIESLIST
@Composable
private fun HomeBody(
    entriesList: List<Entry>,
    getEntryPhotosStream: (Int) -> Flow<List<Photo>>,
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
                getEntryPhotosStream = getEntryPhotosStream,
                modifier = Modifier.padding()
            )
        }
    }
}

// LIST OF ENTRIES
@Composable
private fun JournalEntriesList(
    entriesList: List<Entry>,
    getEntryPhotosStream: (Int) -> Flow<List<Photo>>,
    modifier: Modifier = Modifier
) {
    // LIST OF ALL THE JOURNALENTRY COMPOSABLES!
    LazyColumn( // high-performance vertical scrolling list component, gud for displaying large/dynamic lists
        modifier = modifier,
    ) {
        items(items = entriesList, key = { it.id }) { entry ->
            JournalEntry(
                entry = entry,
                getEntryPhotosStream = getEntryPhotosStream,
                modifier = Modifier
                )
        }
    }


}

// A SINGLE ENTRY
@Composable
private fun JournalEntry(
    entry: Entry,
    getEntryPhotosStream: (Int) -> Flow<List<Photo>>,
    modifier: Modifier = Modifier

) {
    // PHOTO DISPLAY STUFF
    // Each entry collects its own photo stream from db!
    // Get list of photos via viewModel
    // (collectAsState() - converts Flow<List<Photo>> to State<List<Photo>>, triggers recomp/ui update when new data arrives
    //  by - auto-unwraps State to Photo for each photo!)
    val photos by getEntryPhotosStream(entry.id).collectAsState(initial = emptyList())

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
        // PHOTOS DISPLAYED
        LazyRow{
            // for each path in selectedPaths list, display image!
            items(photos) { photo ->
                AsyncImage(
                    model = File(photo.uriString),
                    contentDescription = "Journal entry photo",
                    modifier = Modifier.size(80.dp)
                )
            }
        }
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
            ),
            getEntryPhotosStream = { flowOf(emptyList()) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeBodyEmptyListPreview() {
    JournalAppTheme {
        HomeBody(
            listOf(),
            getEntryPhotosStream = { flowOf(emptyList()) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun JournalEntryPreview() {
    JournalAppTheme {
        JournalEntry(
            Entry(1, "2026-01-12", "Hello my name is Julia."),
            getEntryPhotosStream = { flowOf(emptyList()) }
        )
    }
}