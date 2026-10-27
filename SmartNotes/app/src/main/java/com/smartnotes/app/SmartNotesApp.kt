package com.smartnotes.app

import android.app.Application
import com.smartnotes.app.data.local.NoteDatabase
import com.smartnotes.app.data.repository.NoteRepository

class SmartNotesApp : Application() {

    val database: NoteDatabase by lazy { NoteDatabase.getInstance(this) }
    val noteRepository: NoteRepository by lazy { NoteRepository(database.noteDao()) }
}
