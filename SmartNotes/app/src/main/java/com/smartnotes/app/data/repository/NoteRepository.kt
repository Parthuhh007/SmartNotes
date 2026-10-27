package com.smartnotes.app.data.repository

import com.smartnotes.app.data.local.NoteDao
import com.smartnotes.app.data.model.Note
import com.smartnotes.app.data.model.NoteCategory
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDao) {

    fun observeNotes(query: String, category: NoteCategory): Flow<List<Note>> {
        val trimmedQuery = query.trim()
        val storedCategory = if (category == NoteCategory.ALL) null else category.name
        return noteDao.observeFilteredNotes(trimmedQuery, storedCategory)
    }

    suspend fun getNote(id: Long): Note? = noteDao.getNoteById(id)

    suspend fun save(note: Note): Long {
        return if (note.id == 0L) {
            noteDao.insert(note)
        } else {
            noteDao.update(note)
            note.id
        }
    }

    suspend fun delete(note: Note) = noteDao.delete(note)

    suspend fun deleteById(id: Long) = noteDao.deleteById(id)
}
