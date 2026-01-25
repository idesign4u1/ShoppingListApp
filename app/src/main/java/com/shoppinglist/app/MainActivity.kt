package com.shoppinglist.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shoppinglist.app.ui.screens.auth.AuthScreen
import com.shoppinglist.app.ui.screens.auth.AuthViewModel
import com.shoppinglist.app.ui.screens.chat.ChatScreen
import com.shoppinglist.app.ui.screens.globalchat.GlobalChatScreen
import com.shoppinglist.app.ui.screens.home.HomeScreen
import com.shoppinglist.app.ui.screens.listdetail.ListDetailScreen
import com.shoppinglist.app.ui.theme.ShoppingListTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Request notification permission for Android 13+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
        }
        
        setContent {
            ShoppingListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = hiltViewModel()
                    val authState by authViewModel.uiState.collectAsState()

                    // Determine start destination based on auth state
                    // Use a Loading state to prevent flash
                    if (authState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val startDestination = if (authState.isLoggedIn) "home" else "auth"

                        NavHost(navController = navController, startDestination = startDestination) {
                        
                        composable("auth") {
                            AuthScreen(
                                viewModel = authViewModel,
                                onLoginSuccess = {
                                    navController.navigate("home") {
                                        popUpTo("auth") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("home") {
                            HomeScreen(
                                onNavigateToList = { listId ->
                                    navController.navigate("list/$listId")
                                },
                                onNavigateToGlobalChat = {
                                    navController.navigate("globalchat")
                                },
                                onSignOut = {
                                    navController.navigate("auth") {
                                        popUpTo("home") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable(
                            route = "list/{listId}",
                            arguments = listOf(navArgument("listId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val listId = backStackEntry.arguments?.getString("listId") ?: return@composable
                            ListDetailScreen(
                                onNavigateUp = { navController.navigateUp() },
                                onNavigateToChat = { navController.navigate("chat/$listId") }
                            )
                        }

                        composable(
                            route = "chat/{listId}",
                            arguments = listOf(navArgument("listId") { type = NavType.StringType })
                        ) {
                            ChatScreen(
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }

                        composable("globalchat") {
                            GlobalChatScreen(
                                onNavigateUp = { navController.navigateUp() }
                            )
                        }
                    }
                    }
                }
            }
        }
    }
}
