package com.smartnotes.app.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.smartnotes.app.data.model.Note
import com.smartnotes.app.data.model.NoteCategory
import com.smartnotes.app.data.repository.NoteRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class NotesUiState(
    val notes: List<Note> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: NoteCategory = NoteCategory.ALL,
    val notePendingDelete: Note? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class NotesViewModel(private val repository: NoteRepository) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedCategory = MutableStateFlow(NoteCategory.ALL)
    private val notePendingDelete = MutableStateFlow<Note?>(null)

    private val notes = combine(searchQuery, selectedCategory) { query, category ->
        query to category
    }.flatMapLatest { (query, category) ->
        repository.observeNotes(query, category)
    }

    val uiState: StateFlow<NotesUiState> = combine(
        notes,
        searchQuery,
        selectedCategory,
        notePendingDelete
    ) { noteList, query, category, pending ->
        NotesUiState(
            notes = noteList,
            searchQuery = query,
            selectedCategory = category,
            notePendingDelete = pending
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = NotesUiState()
    )

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
    }

    fun onCategorySelected(category: NoteCategory) {
        selectedCategory.value = category
    }

    fun requestDelete(note: Note) {
        notePendingDelete.value = note
    }

    fun dismissDelete() {
        notePendingDelete.value = null
    }

    fun confirmDelete() {
        val note = notePendingDelete.value ?: return
        viewModelScope.launch {
            repository.delete(note)
            notePendingDelete.value = null
        }
    }

    fun togglePinned(note: Note) {
        viewModelScope.launch {
            repository.save(
                note.copy(
                    isPinned = !note.isPinned,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    companion object {
        fun factory(repository: NoteRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return NotesViewModel(repository) as T
                }
            }
        }
    }
}
