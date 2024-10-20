package com.example.sad.api.devices

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.example.sad.HomeActivity.DevicesViewModel
import com.example.sad.HomeActivity.DevicesViewModelFactory
import com.example.sad.HomeActivity.HomeTopBar
import com.example.sad.HomeActivity.Measurement
import com.example.sad.HomeActivity.parseTimestampToMillis
import com.example.sad.LoginSignup.BottomNavItem
import com.example.sad.LoginSignup.BottomNavigationBar
import com.example.sad.R
import com.example.sad.api.auth.SecureStorage
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceDataScreen(navController: NavController, deviceId: Int) {
    val context = LocalContext.current.applicationContext
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(token, context))
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

    var shownChart by remember { mutableStateOf("") }

    Scaffold(
        topBar = { HomeTopBar("Device data") },
        bottomBar = { BottomNavigationBar(navController, "device_data/$deviceId", bottomNavItems) }
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
                if (shownChart == "Temperature")
                {
                    val yData = measurements.filter{ it.type_name == shownChart}.sortedBy { parseTimestampToMillis(it.timestamp) }
                    val xData = yData.map { it.timestamp }
                    PopupBox(onClickOutside = { shownChart = ""}) {
                        Chart(xAxis = xData, yAxis = yData.map { it.value } , yName = shownChart )
                    }
                }
                MeasurementTable(measurements = measurements, measurementsListState) {
                    shownChart = it
                }
            }
            if (isTopOfList){
                PullRefreshIndicator(refreshing = viewModel.isRefreshing, state = pullRefreshState)
            }
        }
    }
}

@Composable
fun MeasurementTable(measurements: List<Measurement>, state: LazyListState, onTitleClick: (String) -> Unit ) {
    val distinctTypes = measurements.distinctBy { it.type }
    val colNames = distinctTypes.map {
        it.type_name + " " +
                when (it.unit) {
                    "celsius" -> {
                        "(°C)"
                    }
                    "percent" -> {
                        "(%)"
                    }
                    "fahrenheit" -> {
                        "(°F)"
                    }
                    else -> {
                        "(${it.unit})"
                    }
                }
    }

    var measurementsGrouped = emptyList<List<Measurement>>()
    try {
        measurementsGrouped = measurements.chunked(distinctTypes.count())
    } catch (e: Exception){
        // nothing here for now
    }

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
                    modifier = Modifier.weight(1f)
                ){
                    Text("Time", style = MaterialTheme.typography.titleLarge)
                }
                colNames.forEach { colName ->
                    Log.d("COLNAME", colName)
                    Row(
                        modifier = Modifier.weight(1f)
                    ){
                        TextButton(onClick = {
                            val clippedColName = colName.subSequence(0, colName.indexOfFirst { it == ' ' })
                            Log.d("CLICKED TABLE", "COLNAME: $colName; CLIPPED: $clippedColName")
                            onTitleClick(clippedColName.toString())
                        }) {
                            Text(
                                colName.replaceFirstChar {
                                    colName[0].uppercaseChar()
                                },
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                    }
                }
            }
        }
        items(measurementsGrouped) { measurementGroup ->
            MeasurementRow(measurementGroup)
        }
    }
}

@Composable
fun PopupBox(onClickOutside: () -> Unit, content: @Composable () -> Unit ) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.5f))
            .zIndex(10F),
        contentAlignment = Alignment.Center
    ) {
        // popup
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(
                excludeFromSystemGesture = true,
            ),
            // to dismiss on click outside
            onDismissRequest = { onClickOutside() }
        ) {
            Box(
                Modifier
                    .background(Color.White)
                    .clip(RoundedCornerShape(4.dp)),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }

}

@Composable
fun Chart(xAxis: List<String>, yAxis: List<Float>, yName: String) {
    val parameters: List<LineParameters> = listOf(
        LineParameters(
            label = yName,
            data = yAxis.map { it.toDouble() },
            lineColor = Color.Gray,
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        )
    )

    Box(Modifier) {
        LineChart(
            modifier = Modifier.fillMaxSize(),
            linesParameters = parameters,
            isGrid = true,
            gridColor = Color.Blue,
            xAxisData = xAxis,
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
            ),
            xAxisStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Gray,
                fontWeight = FontWeight.W400
            ),
            yAxisRange = 14,
            oneLineChart = false,
            gridOrientation = GridOrientation.VERTICAL
        )
    }
}

@Composable
fun MeasurementRow(measurementGroup: List<Measurement>) {
    val zoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("dd MMM, HH:mm")
    val zonedDateTime = ZonedDateTime.parse(measurementGroup[0].timestamp).withZoneSameInstant(zoneId)
    val formattedTimestamp = formatter.format(zonedDateTime)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onBackground)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Row(
            modifier = Modifier.weight(1f)
        ){
            Text(formattedTimestamp, style = MaterialTheme.typography.bodyLarge)
        }
        measurementGroup.sortedBy { it.type }.forEach { measurement ->
            Row(
                modifier = Modifier.weight(1f)
            ){
                Text("${measurement.value}", style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}
