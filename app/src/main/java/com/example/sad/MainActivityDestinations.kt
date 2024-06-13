package com.example.sad

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

interface Destination{
    val route: String
    val screen: @Composable (NavController) -> Unit
}

object Login: Destination{
    override val route = "login"
    override val screen: @Composable (NavController) -> Unit = {
        LoginScreen(navController = it)
    }
}

object Signup: Destination{
    override val route = "signup"
    override val screen: @Composable (NavController) -> Unit = {
        SignupScreen(navController = it)
    }
}
