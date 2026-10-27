package com.smartnotes.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.smartnotes.app.data.model.Note
import com.smartnotes.app.data.model.NoteCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Note::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: NoteDatabase? = null

        fun getInstance(context: Context): NoteDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context.applicationContext).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): NoteDatabase {
            lateinit var database: NoteDatabase
            database = Room.databaseBuilder(
                context,
                NoteDatabase::class.java,
                "smart_notes.db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            seedIfEmpty(database.noteDao())
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
            return database
        }

        private suspend fun seedIfEmpty(dao: NoteDao) {
            val now = System.currentTimeMillis()
            dao.insert(
                Note(
                    title = "Welcome to Smart Notes",
                    content = "Create, edit, search, and filter notes by category. Everything is stored locally in Room (SQLite).",
                    category = NoteCategory.IDEAS.name,
                    createdAt = now,
                    updatedAt = now,
                    isPinned = true
                )
            )
            dao.insert(
                Note(
                    title = "Project meeting",
                    content = "Prepare the Room database demo: CRUD, search, and category chips.",
                    category = NoteCategory.WORK.name,
                    createdAt = now - 3_600_000,
                    updatedAt = now - 3_600_000
                )
            )
            dao.insert(
                Note(
                    title = "Study checklist",
                    content = "Revise Kotlin coroutines, Flow, and MVVM before the lab session.",
                    category = NoteCategory.STUDY.name,
                    createdAt = now - 7_200_000,
                    updatedAt = now - 7_200_000
                )
            )
        }
    }
}
