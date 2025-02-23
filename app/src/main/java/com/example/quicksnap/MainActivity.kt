package com.example.quicksnap

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quicksnap.ui.theme.QuickSnapTheme

class MainActivity : ComponentActivity() {
    private val notesViewModel: NotesViewModel by viewModels {
        NotesViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "ViewModel initialized")

        setContent {
            QuickSnapTheme {
                QuickSnapApp(notesViewModel)
            }
        }
    }
}

@Composable
fun QuickSnapApp(viewModel: NotesViewModel) {
    val navController = rememberNavController()

    // Collect the notes from the ViewModel as State
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    Log.d("QuickSnapApp", "Notes collected: ${notes.size}")

    NavHost(
        navController = navController,
        startDestination = "notesList"
    ) {
        // Notes List Screen
        composable("notesList") {
            NotesListScreen(
                viewModel = viewModel,
                onNoteClick = { note ->
                    Log.d("QuickSnapApp", "Note clicked: ${note.id}")
                    navController.navigate("editNote/${note.id}")
                }
            )
        }

        // Edit Note Screen
        composable("editNote/{noteId}") { backStackEntry ->
            // Retrieve the note ID from the navigation arguments
            val noteId = backStackEntry.arguments?.getString("noteId")?.toIntOrNull()
            Log.d("QuickSnapApp", "Note ID from args: $noteId")

            BackHandler {
                navController.popBackStack()
            }
            // Find the note by ID from the collected notes list
            val note = notes.firstOrNull { it.id == noteId }
            Log.d("QuickSnapApp", "Note found: ${note != null}")

            if (note != null) {
                EditNoteScreen(
                    note = note,
                    onSave = { updatedContent ->
                        Log.d("QuickSnapApp", "Note updated: $updatedContent")
                        viewModel.updateNote(note, updatedContent)
                        navController.popBackStack()
                    }
                )
            } else {
                Log.d("QuickSnapApp", "Note not found, navigating back")
                navController.popBackStack()
            }
        }
    }
}