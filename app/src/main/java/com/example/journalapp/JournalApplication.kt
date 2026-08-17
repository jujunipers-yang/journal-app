package com.example.journalapp

import android.app.Application
import com.example.journalapp.data.AppContainer
import com.example.journalapp.data.AppDataContainer

/**
 * Custom Application subclass.
 * Creates AppContainer upon startup! (then holds onto it for app's entire lifetime)
 */
class JournalApplication : Application() {

    /**
     * AppContainer instance used by the rest of classes to obtain dependencies
     */
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}