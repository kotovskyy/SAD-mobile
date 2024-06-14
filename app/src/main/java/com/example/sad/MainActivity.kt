package com.example.sad

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sad.HomeActivity.HomeActivity
import com.example.sad.auth.SecureStorage
import com.example.sad.ui.theme.SADTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the token exists and is valid
        val token = SecureStorage.getToken(this)
        if (!token.isNullOrEmpty()) {
            // token found or token is invalid, go to login
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        setContent {
            SADTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Login.route){
        composable(route = Login.route){
            Login.screen(navController)
        }
        composable(route = Signup.route){
            Signup.screen(navController)
        }
    }
}

fun NavController.navigateSingleOnTop(route: String){
    this.navigate(route) {
        launchSingleTop = true
    }
}