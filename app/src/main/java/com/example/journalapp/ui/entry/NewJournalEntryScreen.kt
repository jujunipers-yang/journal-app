package com.example.journalapp.ui.entry

import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.journalapp.R
import com.example.journalapp.data.Entry
import com.example.journalapp.ui.AppViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.journalapp.ui.theme.JournalAppTheme
import kotlinx.coroutines.launch


// NAVIGATION STUFF HERE LATER !!!

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewJournalEntryScreen(
    navigateBack: () -> Unit,
    viewModel: NewJournalEntryViewModel = viewModel(factory = AppViewModelProvider.Factory) // connection to viewModel! (to update/save entry details)
) {
    val coroutineScope = rememberCoroutineScope() // coroutine for save button

    // PHOTO STUFF
    // Registers a photo picker activity launcher in multi-select mode.
    // In this example, the app lets the user select up to 5 media files.
    val pickMultipleMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(5)) { uris ->
            // Callback is invoked after the user selects media items or closes the
            // photo picker.
            if (uris.isNotEmpty()) {
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    Scaffold(

    ) { innerPadding ->
        NewJournalEntryBody(
            entryUiState = viewModel.entryUiState, // connect w viewModel to get entry details!!
            onEntryValueChange = viewModel::updateUiState, // connect w viewModel, update ui state!
            onSaveClick = { // connect w viewModel, save curr ui state to repo!!
                coroutineScope.launch {
                    viewModel.saveItem()
                    viewModel.resetUiState()
                    navigateBack()
                }
            },
            onAddPhotoClick = { pickMultipleMedia.launch(PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageAndVideo)) },
            modifier = Modifier.padding(
                start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                top = innerPadding.calculateTopPadding()
            )
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
        )
    }
}

// BODY (AKA INPUT FORM + SAVE BUTTON + ADD PHOTO BUTTON)
@Composable
fun NewJournalEntryBody(
    entryUiState: EntryUiState,
    onEntryValueChange: (EntryDetails) -> Unit,
    onSaveClick: () -> Unit,
    onAddPhotoClick: () -> Unit,
    modifier: Modifier = Modifier,

) {
    Column(

    ) {
        // INPUT FORM (has its own composable below!)
        EntryInputForm(
            entryDetails = entryUiState.entryDetails, // pass down entryDetails to input form!! (so input can be synced/saved)
            onValueChange = onEntryValueChange,
            modifier = Modifier.fillMaxWidth()
        )

        // SAVE BUTTON
        Button(
            onClick = onSaveClick,
            enabled = entryUiState.isEntryValid
        ) {
            Text(text = stringResource(R.string.save_action))
        }

        // ADD PHOTO BUTTON
        Button(
            onClick = onAddPhotoClick,
        ) {
            Text(text = stringResource(R.string.add_photo_action))
        }
    }
}

// INPUT FORM'S OWN LITTLE COMPOSABLE
@Composable
fun EntryInputForm(
    entryDetails: EntryDetails,
    onValueChange: (EntryDetails) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        // DATE FIELD
        OutlinedTextField(
            value = entryDetails.date,
            onValueChange = { onValueChange(entryDetails.copy(date = it)) }, // on date field changed,
                // update entryDetails with copy of itself, but with the new date value
            label = { Text(stringResource(R.string.entry_date_req)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // TEXT FIELD
        OutlinedTextField(
            value = entryDetails.text,
            onValueChange = { onValueChange(entryDetails.copy(text = it)) }, // on text field changed,
            // update entryDetails with copy of itself, but with the new text value
            label = { Text(stringResource(R.string.entry_text_req)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false
        )
    }
}


@Preview(showBackground = true)
@Composable
private fun NewJournalEntryScreenPreview() {
    JournalAppTheme() {
        NewJournalEntryBody(
            entryUiState = EntryUiState(
                EntryDetails(date = "2026-08-14", text = "It's Chaewon's birthday! Happy birthday to Chaewon wow she's 20!")
            ),
            onEntryValueChange = {}, onSaveClick = {}, onAddPhotoClick = {})
    }
}