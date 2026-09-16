package com.example.journalapp.ui.entry

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.NavigationRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.Navigation
import com.example.journalapp.MAX_PHOTOS_PER_ENTRY
import com.example.journalapp.copyUriToInternalStorage
import com.example.journalapp.ui.AppViewModelProvider
import com.example.journalapp.ui.navigation.NavigationDestination
import com.example.journalapp.ui.theme.JournalAppTheme
import kotlinx.coroutines.launch
import com.example.journalapp.R

object EntryEditDestination : NavigationDestination {
    override val route = "entry_edit"
    override val titleRes = R.string.edit_entry

    const val entryIdArg = "entryId"
    val routeWithArgs = "$route/{$entryIdArg}" // route becomes = "entry_edit_entryId"
}

@Composable
fun EntryEditScreen(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EntryEditViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val coroutineScope = rememberCoroutineScope() // used for save button and delete button! (both suspend funcs in viewModel)

    val context = LocalContext.current // request URI permissions! - for persistent access to photos

    // PHOTO STUFF
    // Registers a photo picker activity launcher in multi-select mode.
    // In this example, the app lets the user select up to 5 media files.
    val pickMultipleMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(MAX_PHOTOS_PER_ENTRY)) { uris ->
            // This callback is invoked AFTER the user selects media items or closes the photo picker!!
            // uris - list of select photos' URIs
            if (uris.isNotEmpty()) {
                val localPaths = uris.map { uri -> copyUriToInternalStorage(context, uri) } // copy image to local storage, and retrieve its path
                viewModel.updateUiStatePhotos(localPaths) // update ui state with new photo paths!
                Log.d("PhotoPicker", "Number of items selected: ${uris.size}")
            } else {
                Log.d("PhotoPicker", "No media selected")
            }
        }

    Scaffold(
        topBar = { },
        modifier = modifier
    ) {
        innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            var deleteConfirmationRequired by rememberSaveable { mutableStateOf(false) }

            // ENTRY EDIT SCREEN'S BODY -> SAME AS BODY IN
            // NEWENTRY SCREEN!! SO JUST CALL THAT!
            NewJournalEntryBody(
                entryUiState = viewModel.entryUiState,
                onEntryValueChange = viewModel::updateUiState,
                onSaveClick = {
                    coroutineScope.launch{
                        viewModel.updateEntry()
                        navigateBack()
                    }
                },
                onAddPhotoClick = {
                    pickMultipleMedia.launch(PickVisualMediaRequest( // launch the callback! (top of class)
                        ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                },
                selectedPaths = viewModel.entryUiState.entryPhotoPaths,
                modifier = Modifier
                    .padding(
                        start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                        top = innerPadding.calculateTopPadding()
                    )
                    .verticalScroll(rememberScrollState())
            )
            // ADDITIONAL DELETE BUTTON!
            Button(
                onClick = {
                    deleteConfirmationRequired = true
                },
                modifier = Modifier
            ) {
                Text(stringResource(R.string.delete_action))
            }
            if(deleteConfirmationRequired) {
                DeleteConfirmationDialog(
                    onDeleteConfirm = {
                        coroutineScope.launch{
                            viewModel.deleteEntry()
                            navigateBack()
                        }
                    },
                    onDeleteCancel = { deleteConfirmationRequired = false }
                )
            }
        }


    }
}

@Composable
private fun DeleteConfirmationDialog(
    onDeleteConfirm: () -> Unit,
    onDeleteCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(onDismissRequest = { /* Do nothing */ },
        title = { Text(stringResource(R.string.attention)) },
        text = { Text(stringResource(R.string.delete_question)) },
        modifier = modifier,
        dismissButton = {
            TextButton(onClick = onDeleteCancel) {
                Text(stringResource(R.string.no))
            }
        },
        confirmButton = {
            TextButton(onClick = onDeleteConfirm) {
                Text(stringResource(R.string.yes))
            }
        })
}

@Preview(showBackground = true)
@Composable
fun EntryEditScreenPreview() {
    JournalAppTheme() {
        EntryEditScreen(navigateBack = {})
    }
}