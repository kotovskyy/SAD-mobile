package com.example.sad.HomeActivity

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sad.api.auth.SecureStorage

@Composable
fun DevicesScreen(navController: NavController){
    val context = LocalContext.current
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(token))
    val devices = viewModel.devices.collectAsState()

    Scaffold(
        topBar = { HomeTopBar("Devices") },
        bottomBar = { HomeBottomNavigationBar(navController = navController, selectedItem = "devices") },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,

                onClick = {

                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    ) { innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            DevicesList(devices = devices.value)
        }
    }
}

@Composable
fun DevicesList(devices: List<Device>){
    LazyColumn {
        items(devices) { device ->
            DeviceItem(device)
        }
    }
}

@Composable
fun DeviceItem(device: Device) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                Toast.makeText(context, "Device ID: ${device.id}", Toast.LENGTH_SHORT).show()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ID: ${device.id}",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "Name: ${device.name}",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "MAC Address: ${device.mac_address}",
                style = MaterialTheme.typography.bodyLarge
            )
            device.type?.let {
                Text(
                    text = "Type: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}