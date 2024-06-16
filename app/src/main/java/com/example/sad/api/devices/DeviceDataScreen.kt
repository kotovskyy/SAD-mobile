package com.example.sad.api.devices

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sad.HomeActivity.DevicesViewModel
import com.example.sad.HomeActivity.DevicesViewModelFactory
import com.example.sad.HomeActivity.HomeTopBar
import com.example.sad.HomeActivity.Measurement
import com.example.sad.LoginSignup.BottomNavItem
import com.example.sad.R
import com.example.sad.api.auth.SecureStorage
import com.example.sad.navigateSingleOnTop
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceDataScreen(navController: NavController, deviceId: Int) {
    val context = LocalContext.current
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(token))
    val device = viewModel.devices.collectAsState().value.find { it.id == deviceId }
    val measurementsListState = rememberLazyListState()
    val isTopOfList by remember {
        derivedStateOf {
            measurementsListState.firstVisibleItemIndex == 0 && measurementsListState.firstVisibleItemScrollOffset == 0
        }
    }
    val pullRefreshState = rememberPullRefreshState(
        refreshing = viewModel.isRefreshing,
        onRefresh = {
            if (isTopOfList){
                viewModel.fetchDeviceMeasurements(deviceId)
            }
        }
    )
    LaunchedEffect(deviceId) {
        viewModel.fetchDeviceMeasurements(deviceId)
    }
    val measurements = viewModel.deviceMeasurements.collectAsState().value


    Scaffold(
        topBar = { HomeTopBar("Device data") },
        bottomBar = { DeviceBottomNavigationBar(navController, "device_data/$deviceId", deviceId) }
    ){ innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .pullRefresh(pullRefreshState),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                MeasurementTable(measurements = measurements, measurementsListState)
            }
            if (isTopOfList){
                PullRefreshIndicator(refreshing = viewModel.isRefreshing, state = pullRefreshState)
            }
        }
    }
}

@Composable
fun MeasurementTable(measurements: List<Measurement>, state: LazyListState) {
    LazyColumn(state = state) {
        item {
            // Define the header of the table
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Row(
                    modifier = Modifier.weight(0.4f)
                ){
                    Text("Timestamp", style = MaterialTheme.typography.titleLarge)
                }
                Row(
                    modifier = Modifier.weight(0.2f)
                ){
                    Text("Value", style = MaterialTheme.typography.titleLarge)
                }
                Row(
                    modifier = Modifier.weight(0.2f)
                ){
                    Text("Type", style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        items(measurements) { measurement ->
            MeasurementRow(measurement)
        }
    }
}

@Composable
fun MeasurementRow(measurement: Measurement) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm:ss")
    val zonedDateTime = ZonedDateTime.parse(measurement.timestamp)
    val formattedTimestamp = formatter.format(zonedDateTime)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onBackground)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.weight(0.4f)
        ){
            Text(formattedTimestamp, style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier.weight(0.2f)
        ){
            Text("${measurement.value}", style = MaterialTheme.typography.bodyLarge)
        }
        Row(
            modifier = Modifier.weight(0.2f)
        ){
            Text("${measurement.type}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun DeviceBottomNavigationBar(navController: NavController, selectedItem: String, deviceId: Int) {

    val bottomNavItems = listOf(
        BottomNavItem(
            title = "Data",
            iconId = R.drawable.data_24,
            route = "device_data/$deviceId"
        ),
        BottomNavItem(
            title = "Settings",
            iconId = R.drawable.settings_24,
            route = "device_settings/$deviceId"
        )
    )

    BottomAppBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(painter = painterResource(id = item.iconId), contentDescription = item.title)
                },
                label = { Text(item.title) },
                selected = selectedItem == item.route,
                onClick = {
                    navController.navigateSingleOnTop(item.route)
                }
            )
        }
    }
}