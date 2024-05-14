package com.example.sad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sad.ui.login.LoginScreen
import com.example.sad.ui.theme.SADTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SADTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SADApp()
                }
            }
        }
    }
}

@Composable
fun SADApp(){
    SADTheme {
        var currentScreen: SADDestination by remember { mutableStateOf(Onboarding) }
        val navController = rememberNavController()

        Column {
            NavHost(
                navController = navController,
                startDestination = Onboarding.route,
            ){
                composable(route = Onboarding.route){
                    Onboarding.screen(navController)
                }
                composable(route = LogIn.route) {
                    LogIn.screen(navController)
                }
            }
        }
    }
}

fun NavController.navigateSingleOnTop(route: String){
    this.navigate(route) {
        launchSingleTop = true
    }
}