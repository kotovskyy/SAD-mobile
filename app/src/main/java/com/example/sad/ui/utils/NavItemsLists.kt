package com.example.sad.ui.utils

import com.example.sad.LoginSignup.BottomNavItem
import com.example.sad.R

val authNavItems = listOf(
    BottomNavItem(
        title = "Login",
        iconId = R.drawable.login_24,
        route = "login"
    ),
    BottomNavItem(
        title = "SignUp",
        iconId = R.drawable.signup_24,
        route = "signup"
    )
)

val homeNavItems = listOf(
    BottomNavItem(
        title = "Devices",
        iconId = R.drawable.devices_24,
        route = "devices"
    ),
    BottomNavItem(
        title = "Home",
        iconId = R.drawable.home_24,
        route = "home"
    ),
    BottomNavItem(
        title = "Profile",
        iconId = R.drawable.profile_24,
        route = "profile"
    )
)
