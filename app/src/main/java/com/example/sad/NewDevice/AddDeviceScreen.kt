package com.example.sad.NewDevice

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.sad.HomeActivity.HomeActivity
import com.example.sad.HomeActivity.HomeTopBar
import com.example.sad.api.auth.SecureStorage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AddDeviceScreen(navController: NavController){
    val context = LocalContext.current.applicationContext
    val token = SecureStorage.getToken(context)

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.CHANGE_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE
        )
    )

    // Request permissions when the Composable enters the composition
    LaunchedEffect(key1 = true) {
        permissionsState.launchMultiplePermissionRequest()
    }

    var wifiSSID: String? = "unknown"
    var selectedTabIndex by remember { mutableStateOf(0)}
    val tabs = listOf("Add new device", "Change Wi-Fi data")

    val viewModel = remember { WifiNetworkViewModel(context) }

    if (permissionsState.allPermissionsGranted) {
        viewModel.fetchCurrentWifiSSID()
        wifiSSID = viewModel.wifiSSID.collectAsState().value
    }

    var deviceName by remember { mutableStateOf("") }
    var wifiName by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }


    Scaffold(
        topBar = { HomeTopBar("Add new device") },
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (wifiSSID == "ESP32"){
                Text("Add new device instructions:", style = MaterialTheme.typography.titleLarge)
                Text("1) Turn on LOCATION while adding a new device")
                Text("2) Turn off CELLULAR DATA while adding a new device")
                Text("3) Open Wi-Fi settings")
                Text("4) Find network with name \"ESP32\"")
                Text("5) Connect to the network")
                Text("6) Enter the password: \"password\"")
                Text("7) Get back to this screen")
                OutlinedButton(onClick = { viewModel.fetchCurrentWifiSSID() } ) {
                    Text("Refresh")
                }
            } else {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = index == selectedTabIndex,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxSize()
                ){
                    when (selectedTabIndex) {
                        0 -> {
                            OutlinedTextField(value = deviceName, onValueChange = { deviceName = it },
                                label = { Text("New device name") }
                            )
                            OutlinedTextField (value = wifiName, onValueChange =
                            { wifiName = it },
                                label = { Text("Wi-Fi to connect") })
                            OutlinedTextField(value = wifiPassword,
                                onValueChange = { wifiPassword = it },
                                label = { Text("Wi-Fi password") }
                            )

                            Button(onClick = {
                                Toast.makeText(context, "New device added", Toast.LENGTH_SHORT).show()
                                connectToESP32(
                                    deviceName.trim(),
                                    wifiName.trim(),
                                    wifiPassword.trim(),
                                    token?.trim(),
                                    action = selectedTabIndex
                                )
//                        val intent = Intent(context, HomeActivity::class.java)
//                        context.startActivity(intent)
                            }) {
                                Text("Add new device")
                            }
                        }
                        1 -> {
                            OutlinedTextField (value = wifiName, onValueChange =
                            { wifiName = it },
                                label = { Text("Wi-Fi to connect") })
                            OutlinedTextField(value = wifiPassword,
                                onValueChange = { wifiPassword = it },
                                label = { Text("Wi-Fi password") }
                            )

                            Button(onClick = {
                                Toast.makeText(context, "Config updated", Toast.LENGTH_SHORT).show()
                                connectToESP32(
                                    wifiName = wifiName.trim(),
                                    wifiPassword =  wifiPassword.trim(),
                                    action = selectedTabIndex
                                )
                            }) {
                                Text("Update")
                            }
                        }
                    }
                }
            }
        }
    }
}

fun connectToESP32(deviceName: String="", wifiName: String="", wifiPassword: String="", token: String?="", action: Int = 0) {
    val thread = Thread {
        try {
            val socket = Socket("192.168.4.1", 8080) // The IP address and port of the ESP32
            val output = PrintWriter(socket.getOutputStream(), true)
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))

            // Send a message to the ESP32
            output.println("ACTION=$action;SSID=$wifiName;PASSWORD=$wifiPassword;DEVICE_NAME=$deviceName;TOKEN=$token")
            val response = input.readLine() // Read the response from ESP32
            println("Response from ESP32: $response")

            socket.close()
        } catch (e: Exception) {
            e.printStackTrace() // Handle exceptions
        }
    }
    thread.start()
}

//@OptIn(ExperimentalPermissionsApi::class)
//@Composable
//fun PermissionsStatus(permissionsState: MultiplePermissionsState) {
//    Column {
//        permissionsState.permissions.forEach { perm ->
//            when (perm.status) {
//                is PermissionStatus.Granted -> {
//                    Text(text = "Permission granted: ${perm.permission}", style = MaterialTheme.typography.bodyLarge)
//                }
//                is PermissionStatus.Denied -> {
//                    Text(text = "Permission denied: ${perm.permission}. Need permission for location access.", style = MaterialTheme.typography.bodyLarge)
//                }
//            }
//        }
//    }
//}