package dev.khoi.notevibe.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.khoi.notevibe.ui.screens.LoginScreen
import dev.khoi.notevibe.ui.screens.NoteListScreen
import dev.khoi.notevibe.ui.theme.NoteVibeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NoteVibeTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen (
                            onLoginSuccess = { navController.navigate("note_list") { popUpTo("login") { inclusive = true } } }
                        )
                    }
                    composable("note_list") {
                        NoteListScreen(
                            onAddNote = { navController.navigate("note_detail/") },
                            onEditNote = { noteId -> navController.navigate("note_detail/$noteId") }
                        )
                    }
                    composable("note_detail/{noteId}") { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getString("noteId")
                        NoteDetailScreen(
                            noteId = noteId,
                            onSave = { navController.popBackStack() }
                        )
                    }
                    composable("note_detail/") {
                        NoteDetailScreen(
                            noteId = null,
                            onSave = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}