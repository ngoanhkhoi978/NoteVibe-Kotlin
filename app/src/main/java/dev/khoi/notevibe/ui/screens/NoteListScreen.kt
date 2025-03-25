package dev.khoi.notevibe.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.khoi.notevibe.R
import dev.khoi.notevibe.domain.model.Note
import dev.khoi.notevibe.ui.NoteViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    onAddNote: () -> Unit,
    onEditNote: (String) -> Unit
) {
    val viewModel: NoteViewModel = viewModel()
    val notes by viewModel.notes.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.firebaselogo),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = "NoteVibe",
                    color = Color(247, 201, 84),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNote,
                content = { Icon(Icons.Default.Add, contentDescription = "Add Note", tint = Color.White) },
                shape = CircleShape,
                containerColor = Color(255, 137, 0)
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .background(Color(49, 63, 83))
                .padding(horizontal = 8.dp)
        ) {
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No notes yet",color = Color.LightGray, style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(notes) { note ->
                        NoteItem(
                            note = note,
                            onEdit = { onEditNote(note.id) },
                            onDelete = { viewModel.deleteNote(note.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NoteItem(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .height(96.dp)
            .clickable { onEdit() }

        ,
        colors = CardDefaults.cardColors(
            containerColor = Color(72, 84, 102)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = note.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = Color.White
                )

                if (note.description.isNotBlank()) {
                    Text(
                        text = note.description,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp,
                        color = Color(159, 166, 178)
                    )
                }
            }
        }
    }
}
