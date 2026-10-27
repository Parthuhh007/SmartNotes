package com.smartnotes.app.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class NoteCategoryTest {

    @Test
    fun persistedValues_excludeAllFilter() {
        val persisted = NoteCategory.persistedValues()
        assertFalse(persisted.contains(NoteCategory.ALL))
        assertEquals(5, persisted.size)
    }

    @Test
    fun fromStorage_fallsBackToOther() {
        assertEquals(NoteCategory.WORK, NoteCategory.fromStorage("WORK"))
        assertEquals(NoteCategory.OTHER, NoteCategory.fromStorage("unknown"))
    }
}
