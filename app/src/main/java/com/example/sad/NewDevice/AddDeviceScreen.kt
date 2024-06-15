package com.example.sad.NewDevice

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.sad.HomeActivity.HomeTopBar

@Composable
fun AddDeviceScreen(navController: NavController){
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
            Text("Add new device instructions:", style = MaterialTheme.typography.titleLarge)
            Text("1) Open Wi-Fi settings")
            Text("2) Find network with name \"ESP32\"")
            Text("3) Connect to the network")
            Text("4) Get back to this screen")
        }
    }
}