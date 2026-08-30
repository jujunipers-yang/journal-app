package com.example.journalapp

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * While app has temporary access to photo URI (aka right after photo picking), copy the image bytes and store in app's
 * private storage directory. Returns the path to that directory.
 * (called by NewJournalEntryScreen - copy image bytes right after user finishes picking, and store
 * its local path in db!)
 */
fun copyUriToInternalStorage(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri) // while u still hv temp access to URI (right when Picker gives it to u), open a readable stream of the image bytes at that URI(/address)
    val fileName = "photo_${System.currentTimeMillis()}_${(0..9999).random()}.jpg" // generates unique fileName (timestamp + random #)
    val file = File(context.filesDir, fileName) // creates ref to a new location (to fileName) inside ur app's private storage directory (context.filesDir)
    inputStream?.use { input -> // copying - reads image bytes from source (picker's URI) and writes into ur new file
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file.absolutePath // return path to ur new copy of image bytes!
}

/**
 * PHOTO PICKER ISSUES ON MY ANDROID SOFTWARE V10!
 *
 * Problem:
 * When u pick a photo using Photo Picker, u get a URI - the address pointing to that where that image
 * lives (managed by some other app/ur provider, ie your gallery app or Google Photos).
 * So, ur app is just a 'guest' with *temporary* access to these URIs.
 * Normally, to persist photos across app restarts, apps will need *permanent* access - do this by
 * requesting URI permissions, aka calling takePersistableUriPermissions().
 * However, on some devices (like mine, Android 10 via the backported Photo Picker), this request
 * gets rejected - so, cannot get permanent access to photo URIs, thus photos in app don't persist
 * after app restarts.
 * (how this looked like in testing: photo URIs got saved as Strings just fine to db, as all that can happen
 * immediately right after picking the photos. Then, display just fine in HomeScreen. But, after closing the app
 * and returning, photos don't display in HomeScreen - tho app has URI for each photo, it's unable to access these
 * again bc it doesn't hv permission (unless u make a new entry and get temp access, in that new entry, and select
 * new photos))
 *
 * Solution:
 * Instead of saving a *reference* to where the photo lives (URI), copy the *actual image data* into a file
 * ur app creates and owns, inside ur app's private storage folder (context.filesDir)! -> own instead of asking for permission
 * Once that copy exists, u don't need the URI permission anymore - temporary access is enough to retrieve + store
 * a copy of the image data, which can then be displayed by the app anytime.
 *
 * Why this is a better approach than the first:
 * Ur app now owns the image data outright - no permission grants to request, and does not depend on
 * how a specific device's Photo Picker implementation happens to hand back URIs.
 * This makes ur app work regardless of Android version (thus regardless of whether the Photo Picker is
 * native or backported)!
 */