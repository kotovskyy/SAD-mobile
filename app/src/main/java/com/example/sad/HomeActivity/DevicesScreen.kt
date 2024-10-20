package com.example.sad.HomeActivity

import android.app.Application
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sad.LoginSignup.BottomNavigationBar
import com.example.sad.NewDevice.AddDeviceActivity
import com.example.sad.SADApplication
import com.example.sad.api.auth.SecureStorage
import com.example.sad.api.devices.DeviceData
import com.example.sad.navigateSingleOnTop
import com.example.sad.room.Offline_SAD_Repository
import com.example.sad.ui.utils.homeNavItems
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevicesScreen(navController: NavController){
    val context = LocalContext.current
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(
        factory = DevicesViewModelFactory(token, context.applicationContext)
    )
    val devicesListState = rememberLazyListState()
    val isTopOfList by remember {
        derivedStateOf {
            devicesListState.firstVisibleItemIndex == 0 && devicesListState.firstVisibleItemScrollOffset == 0
        }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = {
            if (isTopOfList){
                viewModel.fetchDevices(context)
            }
        }
    )
    LaunchedEffect(Unit) {
        viewModel.fetchDevices(context)
    }
    val devices = viewModel.devices.collectAsState()
    val latestMeasurements = viewModel.latestMeasurements

    Scaffold(
        topBar = { HomeTopBar("Devices") },
        bottomBar = { BottomNavigationBar(navController,"devices", homeNavItems) },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,

                onClick = {
                    val intent = Intent(context, AddDeviceActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState),
            contentAlignment = Alignment.TopCenter
        ){
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                DevicesList(
                    devices = devices.value,
                    latestMeasurements = latestMeasurements,
                    navController,
                    devicesListState,
                    deleteDevice = { deviceId ->
                        viewModel.deleteDevice(deviceId)
                    }
                )
            }
            if (isTopOfList){
                PullRefreshIndicator(refreshing = viewModel.isRefreshing, state = pullRefreshState)
            }
        }
    }
}

@Composable
fun DevicesList(
    devices: List<Device>,
    latestMeasurements: List<LatestMeasurement>,
    navController: NavController,
    state: LazyListState,
    deleteDevice: (deviceId: Int) -> Unit
) {
    LazyColumn(state = state) {
        items(devices) { device ->
            val latestMeasurement = latestMeasurements.find { it.deviceID == device.id }
            DeviceItem(device, latestMeasurement, navController, deleteDevice)
        }
    }
}

@Composable
fun DeviceItem(
    device: Device,
    latestMeasurement: LatestMeasurement?,
    navController: NavController,
    deleteDevice: (deviceId: Int) -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog){
        AlertDialog(
            onDismissRequest = { /*TODO*/ },
            confirmButton = {
                Button(
                    onClick = {
                        deleteDevice(device.id)
                        showDialog = false
                    }
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                    }
                ) {
                    Text(text = "Cancel")
                }
            },
            title = { Text(text = "Delete ${device.name} device?") },
            text = { Text(text = "This action cannot be undone.") },
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigateSingleOnTop("device_data/${device.id}")
                Toast
                    .makeText(context, "Device ID: ${device.id}", Toast.LENGTH_SHORT)
                    .show()
            },
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ){
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(
                        text = device.name,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Device"
                        )
                    }
                }

                latestMeasurement?.let { measurement ->
                    val formattedTimestamp = try {
                        // Safely parse and format the timestamp
                        val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm:ss")
                        val zonedDateTime = ZonedDateTime.parse(measurement.timestamp)
                        formatter.format(zonedDateTime)
                    } catch (e: Exception) {
                        // Fallback if the timestamp parsing fails
                        "Unknown time"
                    }

                    Text(text = "Temperature: ${measurement.temperatureValue}°C", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Humidity: ${measurement.humidityValue}%", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Last updated: $formattedTimestamp", style = MaterialTheme.typography.bodyMedium)
                } ?: run {
                    // Handle the case where `latestMeasurement` is null
                    Text(text = "No measurement data available", style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}