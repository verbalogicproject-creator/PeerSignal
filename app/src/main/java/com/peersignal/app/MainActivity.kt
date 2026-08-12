package com.peersignal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.peersignal.app.theme.PeerSignalTheme
import com.peersignal.app.ui.navigation.PeerSignalNavGraph
import com.peersignal.app.ui.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ensure Edge-to-Edge is enabled at the Activity level
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            PeerSignalTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                icon = { Text("📡") },
                                label = { Text("Beacon") },
                                selected = false,
                                onClick = { navController.navigate(Screen.Beacon.route) }
                            )
                            NavigationBarItem(
                                icon = { Text("🧪") },
                                label = { Text("Sandbox") },
                                selected = false,
                                onClick = { navController.navigate(Screen.Sandbox.route) }
                            )
                            NavigationBarItem(
                                icon = { Text("👤") },
                                label = { Text("Profile") },
                                selected = false,
                                onClick = { navController.navigate(Screen.Profile.route) }
                            )
                        }
                    }
                ) { innerPadding ->
                    PeerSignalNavGraph(
                        navController = navController,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
