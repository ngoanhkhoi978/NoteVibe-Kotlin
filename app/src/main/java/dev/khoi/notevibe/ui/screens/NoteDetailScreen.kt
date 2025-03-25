package dev.khoi.notevibe.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import dev.khoi.notevibe.R
import dev.khoi.notevibe.domain.model.Note
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailScreen(
    noteId: String?,
    onSave: () -> Unit,
) {
    val viewModel: NoteViewModel = viewModel()
    val notes by viewModel.notes.collectAsState()
    val note = notes.find { it.id == noteId }
    val scope = rememberCoroutineScope()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) } // Trạng thái hiển thị dialog xác nhận xóa

    LaunchedEffect(note) {
        title = note?.title ?: ""
        description = note?.description ?: ""
        imageUri = note?.image
    }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            println("Selected image URI: $it")
            imageUri = it.toString()
        }
    }

    Scaffold(
        topBar = {
            Box(modifier = Modifier.background(Color(49, 63, 83))) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .align(Alignment.Center),
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onSave) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    if (noteId != null) {
                        IconButton(onClick = { showDeleteDialog = true }) { // Hiển thị dialog khi nhấn Delete
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(255, 116, 97),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier)
                    }
                }
            }
            // Dialog xác nhận xóa
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Confirm Delete") },
                    text = { Text("Are you sure you want to delete this note?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    viewModel.deleteNote(noteId ?: "")
                                    showDeleteDialog = false
                                    onSave()
                                }
                            }
                        ) {
                            Text("Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(49, 63, 83))
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title", color = Color.White) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(255, 187, 12),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description", color = Color.White) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(255, 187, 12),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton (
                onClick = {
                    pickImageLauncher.launch("image/*")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                border = BorderStroke(1.dp, Color(255, 137, 0)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = if (noteId != null) "Edit image" else "Upload image", color = Color(255, 137, 0))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isSaving = true
                    scope.launch {
                        try {
                            println("Saving note with image: $imageUri")
                            val updatedNote = Note(
                                id = noteId ?: "",
                                title = title,
                                description = description,
                                image = imageUri
                            )
                            viewModel.saveNote(updatedNote)
                            println("Save completed successfully")
                            onSave()
                        } catch (e: Exception) {
                            println("Error during save: ${e.message}")
                            e.printStackTrace()
                        } finally {
                            isSaving = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                enabled = title.isNotBlank() && !isSaving,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(255, 137, 0)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSaving) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "SAVING",
                            fontSize = 22.sp,
                            letterSpacing = 2.sp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                } else {
                    Text(
                        "SAVE",
                        fontSize = 22.sp,
                        letterSpacing = 2.sp
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            if (noteId == null) {
                Text(
                    text = "Image: ${imageUri ?: "No image selected"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }

            note?.image?.let { imageUrl ->
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "Note Image",
                    modifier = Modifier.fillMaxWidth().height(500.dp)
                )

            }
        }
    }
}