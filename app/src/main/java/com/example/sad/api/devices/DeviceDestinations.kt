package com.example.sad.api.devices

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.sad.Destination
import com.example.sad.HomeActivity.DevicesScreen

interface DeviceDestination{
    val route: String
    val screen: @Composable (NavController, Int) -> Unit
}

object DeviceData : DeviceDestination {
    override val route = "device_data/{deviceId}"
    override val screen: @Composable (NavController, Int) -> Unit = { navController, deviceId ->
        DeviceDataScreen(navController = navController, deviceId = deviceId)
    }
}

object DeviceSettigns : DeviceDestination {
    override val route = "device_settings/{deviceId}"
    override val screen: @Composable (NavController, Int) -> Unit = { navController, deviceId ->
        DeviceSettingsScreen(navController = navController, deviceId = deviceId)
    }
}


//object DeviceSettings : DeviceDestination {
//    override val route = "device_settings/{deviceId}"
//    override val screen: @Composable (NavController, String?) -> Unit = { navController, deviceId ->
//        DeviceSettingsScreen(navController = navController, deviceId = deviceId?.toIntOrNull() ?: -1)
//    }
//}