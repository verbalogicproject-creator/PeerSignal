package com.peersignal.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.peersignal.app.ui.beacon.BeaconStreamScreen
import com.peersignal.app.ui.connect.ConnectScreen
import com.peersignal.app.ui.profile.ProfileScreen
import com.peersignal.app.ui.sandbox.SandboxScreen

sealed class Screen(val route: String) {
    object Sandbox : Screen("sandbox")
    object Beacon : Screen("beacon")
    object Connect : Screen("connect")
    object Profile : Screen("profile")
}

@Composable
fun PeerSignalNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screen.Beacon.route, modifier = modifier) {
        composable(Screen.Sandbox.route) {
            SandboxScreen()
        }
        composable(Screen.Beacon.route) {
            BeaconStreamScreen(
                onNavigateToConnect = { navController.navigate(Screen.Connect.route) }
            )
        }
        composable(Screen.Connect.route) {
            ConnectScreen()
        }
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
    }
}
