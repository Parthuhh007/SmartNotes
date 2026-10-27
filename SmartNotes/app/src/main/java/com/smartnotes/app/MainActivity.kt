package com.smartnotes.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.smartnotes.app.ui.editor.NoteEditorScreen
import com.smartnotes.app.ui.editor.NoteEditorViewModel
import com.smartnotes.app.ui.notes.NotesListScreen
import com.smartnotes.app.ui.notes.NotesViewModel
import com.smartnotes.app.ui.theme.SmartNotesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val repository = (application as SmartNotesApp).noteRepository
        setContent {
            SmartNotesTheme {
                SmartNotesNavHost(repositoryProvider = { repository })
            }
        }
    }
}

@Composable
private fun SmartNotesNavHost(
    repositoryProvider: () -> com.smartnotes.app.data.repository.NoteRepository
) {
    val navController = rememberNavController()
    val repository = repositoryProvider()

    NavHost(navController = navController, startDestination = "notes") {
        composable("notes") {
            val viewModel: NotesViewModel = viewModel(factory = NotesViewModel.factory(repository))
            NotesListScreen(
                viewModel = viewModel,
                onAddNote = { navController.navigate("editor/0") },
                onOpenNote = { id -> navController.navigate("editor/$id") }
            )
        }
        composable(
            route = "editor/{noteId}",
            arguments = listOf(navArgument("noteId") { type = NavType.LongType })
        ) {
            val viewModel: NoteEditorViewModel = viewModel(
                factory = NoteEditorViewModel.factory(repository)
            )
            NoteEditorScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
