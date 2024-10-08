package com.example.sad

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.sad.NewDevice.AddDeviceScreen
import com.example.sad.HomeActivity.DevicesScreen
import com.example.sad.HomeActivity.HomeScreen
import com.example.sad.HomeActivity.ProfileScreen
import com.example.sad.LoginSignup.LoginScreen
import com.example.sad.LoginSignup.SignupScreen

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

object Home : Destination {
    override val route = "home"
    override val screen: @Composable (NavController) -> Unit = {
        HomeScreen(navController = it)
    }
}

object Profile : Destination {
    override val route = "profile"
    override val screen: @Composable (NavController) -> Unit = {
        ProfileScreen(navController = it)
    }
}

object Devices : Destination {
    override val route = "devices"
    override val screen: @Composable (NavController) -> Unit = {
        DevicesScreen(navController = it)
    }
}

object AddDevice : Destination {
    override val route = "addDevice"
    override val screen: @Composable (NavController) -> Unit = {
        AddDeviceScreen(navController = it)
    }
}
