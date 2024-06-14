package com.example.sad.HomeActivity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sad.Devices
import com.example.sad.Home
import com.example.sad.Profile
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
    }
}
