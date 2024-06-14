package com.example.sad.api.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sad.HomeActivity.DevicesViewModel
import com.example.sad.HomeActivity.DevicesViewModelFactory
import com.example.sad.HomeActivity.HomeTopBar
import com.example.sad.LoginSignup.BottomNavItem
import com.example.sad.R
import com.example.sad.api.auth.SecureStorage
import com.example.sad.navigateSingleOnTop

@Composable
fun DeviceSettingsScreen(navController: NavController, deviceId: Int) {
    // Implementation of the device details UI
    Scaffold(
        topBar = { HomeTopBar("Device settings") },
        bottomBar = { DeviceBottomNavigationBar(navController, "device_settings/$deviceId", deviceId) }
    ){ innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

        }
    }
}
