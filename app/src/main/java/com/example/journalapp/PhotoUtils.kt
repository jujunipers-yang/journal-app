package com.example.journalapp

import android.content.Context
import android.net.Uri
import java.io.File

fun copyUriToInternalStorage(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    val fileName = "photo_${System.currentTimeMillis()}_${(0..9999).random()}.jpg"
    val file = File(context.filesDir, fileName)
    inputStream?.use { input ->
        file.outputStream().use { output -> input.copyTo(output) }
    }
    return file.absolutePath
}