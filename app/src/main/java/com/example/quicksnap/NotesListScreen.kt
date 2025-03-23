package com.example.quicksnap

import android.app.Activity
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

private fun exitApp(context: Context) {
    (context as? Activity)?.finishAffinity()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickSnapTopAppBar(
    modifier: Modifier = Modifier, // Add a modifier parameter for flexibility
    title: String = stringResource(R.string.app_name)
) {
    val context = LocalContext.current

    BackHandler {
        exitApp(context)
    }
    TopAppBar(
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {exitApp(context)}) {
                    Icon(painterResource(R.drawable.baseline_arrow_back_24),
                        contentDescription = "Back button")
                }
                Image(
                    painter = painterResource(R.drawable.ic_notes),
                    contentDescription = stringResource(R.string.quick_snap_logo_description),
                    modifier = Modifier
                        .size(40.dp) // Adjusted size
                        .clip(CircleShape),
                )
                Column(modifier = Modifier.padding(start = 8.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ), modifier = modifier // Use the passed modifier
    )
}

@Composable
fun AddNoteFloatingActionButton(
    newNoteInputText: String,
    onNewNoteInputTextChanged: (String) -> Unit,
    onAddNote: (String) -> Unit
) {
    FloatingActionButton(
        onClick = {
            if (newNoteInputText.isNotBlank()) {
                onAddNote(newNoteInputText)
                onNewNoteInputTextChanged("")
            }
        },
        containerColor = MaterialTheme.colorScheme.primary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp)
    ) {
        Icon(
            Icons.Default.Edit,
            contentDescription = "Add Note",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(viewModel: NotesViewModel, onNoteClick: (Note) -> Unit) {
    val notes by viewModel.notes.collectAsState(initial = emptyList())
    var newNoteInputText by remember { mutableStateOf("") }

    Scaffold(
        contentWindowInsets = WindowInsets.safeContent,
        topBar = {
            QuickSnapTopAppBar()
        },
        floatingActionButton = {
            AddNoteFloatingActionButton(
                newNoteInputText = newNoteInputText,
                onNewNoteInputTextChanged = { newNoteInputText = it },
                onAddNote = viewModel::addNote
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = newNoteInputText,
                onValueChange = { newNoteInputText = it },
                label = { Text("Add a note") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (newNoteInputText.isNotBlank()) {
                            viewModel.addNote(newNoteInputText)
                            newNoteInputText = ""
                        }
                    }
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (notes.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.empty_note),
                        contentDescription = "Creepy Empty Note",
                        modifier = Modifier
                            .clip(CircleShape)
                            .size(250.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No notes yet... Start adding some!")
                }
            } else {
                LazyColumn {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onDelete = { viewModel.deleteNote(note) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(
    note: Note,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDarkTheme = isSystemInDarkTheme()
    val cardShape = RoundedCornerShape(12.dp)
    val cardBackgroundColor = MaterialTheme.colorScheme.surface
    val borderColor = if (isDarkTheme) {
        MaterialTheme.colorScheme.surfaceVariant // Darker border in dark mode
    } else {
        Color.LightGray // Lighter border in light mode
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        shape = cardShape,
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NotePreview(
                    note = note.content,
                    modifier = Modifier.weight(1f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    IconButton(onClick = onClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Note",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun NotePreview(note: String, modifier: Modifier = Modifier) {
    val (title, body) = extractTitleAndBody(note)

    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        if (body.isNotBlank()) {
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                thickness = 1.dp
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                minLines = 2,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun extractTitleAndBody(note: String): Pair<String, String> {
    if (note.isEmpty()) {
        return "" to ""
    }

    val parts = note.split(" ", limit = 3)
    return when (parts.size) {
        1 -> parts[0] to ""
        2 -> parts.joinToString(" ") to ""
        else -> parts.take(2).joinToString(" ") to parts.drop(2).joinToString(" ")
    }
}