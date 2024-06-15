package com.example.sad.NewDevice

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
import com.example.sad.AddDevice
import com.example.sad.ui.theme.SADTheme

class AddDeviceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SADTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddDeviceNavigation()
                }
            }
        }
    }
}

@Composable
fun AddDeviceNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AddDevice.route){
        composable(AddDevice.route){
            AddDevice.screen(navController)
        }
    }
}