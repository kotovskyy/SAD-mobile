package com.example.sad.HomeActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sad.Devices
import com.example.sad.Home
import com.example.sad.Profile
import com.example.sad.api.devices.DeviceData
import com.example.sad.api.devices.DeviceSettigns
import com.example.sad.ui.theme.SADTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SADTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HomeActivityNavigation()
                }
            }
        }
     }
}

@Composable
fun HomeActivityNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home.route){
        composable(route = Home.route) {
            HomeScreen(navController = navController)
        }
        composable(route = Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(route = Devices.route) {
            DevicesScreen(navController = navController)
        }
        composable(route = DeviceData.route,
            arguments = listOf(navArgument("deviceId") { type = NavType.IntType })) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getInt("deviceId") ?: -1  // Handling default or error case
            DeviceData.screen(navController, deviceId)
        }
        composable(route = DeviceSettigns.route,
            arguments = listOf(navArgument("deviceId") { type = NavType.IntType })) { backStackEntry ->
            val deviceId = backStackEntry.arguments?.getInt("deviceId") ?: -1  // Handling default or error case
            DeviceSettigns.screen(navController, deviceId)
        }
    }
}
