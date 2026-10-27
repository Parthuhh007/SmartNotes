package com.smartnotes.app.data.model

enum class NoteCategory(val displayName: String) {
    ALL("All"),
    PERSONAL("Personal"),
    WORK("Work"),
    STUDY("Study"),
    IDEAS("Ideas"),
    OTHER("Other");

    companion object {
        fun persistedValues(): List<NoteCategory> = entries.filter { it != ALL }

        fun fromStorage(value: String): NoteCategory {
            return persistedValues().firstOrNull { it.name == value } ?: OTHER
        }
    }
}
