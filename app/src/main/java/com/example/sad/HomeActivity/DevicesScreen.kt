package com.example.sad.HomeActivity

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.material3.FloatingActionButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sad.NewDevice.AddDeviceActivity
import com.example.sad.api.auth.SecureStorage
import com.example.sad.api.devices.DeviceData
import com.example.sad.navigateSingleOnTop

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DevicesScreen(navController: NavController){
    val context = LocalContext.current
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(token))
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
                viewModel.fetchDevices()
            }
        }
    )
    LaunchedEffect(Unit) {
        viewModel.fetchDevices()
    }
    val devices = viewModel.devices.collectAsState()

    Scaffold(
        topBar = { HomeTopBar("Devices") },
        bottomBar = { HomeBottomNavigationBar(navController = navController, selectedItem = "devices") },
        floatingActionButton = {
            FloatingActionButton(
                shape = CircleShape,

                onClick = {
                    val intent = Intent(context, AddDeviceActivity::class.java)
                    context.startActivity(intent)
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
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
                DevicesList(devices = devices.value, navController, devicesListState)
            }
            if (isTopOfList){
                PullRefreshIndicator(refreshing = viewModel.isRefreshing, state = pullRefreshState)
            }
        }
    }
}

@Composable
fun DevicesList(devices: List<Device>, navController: NavController, state: LazyListState){
    LazyColumn(state = state) {
        items(devices) { device ->
            DeviceItem(device, navController)
        }
    }
}

@Composable
fun DeviceItem(device: Device, navController: NavController) {
    val context = LocalContext.current
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