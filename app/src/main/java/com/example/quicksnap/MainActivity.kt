package com.example.quicksnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quicksnap.ui.theme.QuickSnapTheme

class MainActivity : ComponentActivity() {
    private val notesViewModel: NotesViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    NavHost(navController = navController, startDestination = "notesList") {
        composable("notesList") {
            NotesListScreen(
                viewModel = viewModel,
                onNoteClick = { note ->
                    navController.navigate("editNote/$note")
                }
            )
        }
        composable("editNote/{note}") { backStackEntry ->
            val note = backStackEntry.arguments?.getString("note") ?: ""
            EditNoteScreen(
                originalNote = note,
                onSave = { editedNote ->
                    viewModel.updateNote(oldNote = note, newNote = editedNote)
                    navController.popBackStack()
                }
            )
        }
    }
}