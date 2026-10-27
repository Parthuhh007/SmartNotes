package com.smartnotes.app.ui.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.smartnotes.app.data.model.Note
import com.smartnotes.app.data.model.NoteCategory
import com.smartnotes.app.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NoteEditorUiState(
    val noteId: Long = 0L,
    val title: String = "",
    val content: String = "",
    val category: NoteCategory = NoteCategory.PERSONAL,
    val isPinned: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class NoteEditorViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: NoteRepository
) : ViewModel() {

    private val noteId: Long = savedStateHandle.get<Long>("noteId") ?: 0L

    private val _uiState = MutableStateFlow(NoteEditorUiState(noteId = noteId))
    val uiState: StateFlow<NoteEditorUiState> = _uiState.asStateFlow()

    init {
        if (noteId == 0L) {
            _uiState.update { it.copy(isLoading = false) }
        } else {
            viewModelScope.launch {
                val note = repository.getNote(noteId)
                if (note == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Note not found")
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            noteId = note.id,
                            title = note.title,
                            content = note.content,
                            category = NoteCategory.fromStorage(note.category),
                            isPinned = note.isPinned,
                            createdAt = note.createdAt,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { it.copy(title = value, errorMessage = null) }
    }

    fun onContentChange(value: String) {
        _uiState.update { it.copy(content = value, errorMessage = null) }
    }

    fun onCategoryChange(category: NoteCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onPinnedChange(pinned: Boolean) {
        _uiState.update { it.copy(isPinned = pinned) }
    }

    fun save() {
        val current = _uiState.value
        val title = current.title.trim()
        val content = current.content.trim()
        if (title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Title cannot be empty") }
            return
        }
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            repository.save(
                Note(
                    id = current.noteId,
                    title = title,
                    content = content,
                    category = current.category.name,
                    createdAt = if (current.noteId == 0L) now else current.createdAt,
                    updatedAt = now,
                    isPinned = current.isPinned
                )
            )
            _uiState.update { it.copy(isSaved = true) }
        }
    }

    companion object {
        fun factory(repository: NoteRepository): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    modelClass: Class<T>,
                    extras: CreationExtras
                ): T {
                    return NoteEditorViewModel(extras.createSavedStateHandle(), repository) as T
                }
            }
        }
    }
}
