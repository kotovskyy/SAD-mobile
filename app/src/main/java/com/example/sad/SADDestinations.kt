package com.example.sad

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.sad.ui.login.LoginScreen
import com.example.sad.ui.onboarding.OnboardingScreen
import com.example.sad.ui.signup.SignupScreen

interface SADDestination{
    val route: String
    val screen: @Composable (NavController) -> Unit
}

object Onboarding : SADDestination {
    override val route = "onboarding"
    override val screen: @Composable (NavController) -> Unit = { OnboardingScreen(it) }
}

object LogIn : SADDestination {
    override val route = "login"
    override val screen: @Composable (NavController) -> Unit = { LoginScreen(it) }
}

object SignUp : SADDestination {
    override val route = "signup"
    override val screen: @Composable (NavController) -> Unit = { SignupScreen(it) }
}
