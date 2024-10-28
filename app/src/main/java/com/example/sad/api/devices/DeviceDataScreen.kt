package com.example.sad.api.devices

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sad.HomeActivity.DevicesViewModel
import com.example.sad.HomeActivity.DevicesViewModelFactory
import com.example.sad.HomeActivity.HomeTopBar
import com.example.sad.HomeActivity.Measurement
import com.example.sad.LoginSignup.BottomNavItem
import com.example.sad.LoginSignup.BottomNavigationBar
import com.example.sad.R
import com.example.sad.api.auth.SecureStorage
import com.example.sad.lineChart.LineChart
import com.example.sad.lineChart.baseComponents.model.GridOrientation
import com.example.sad.lineChart.model.LineParameters
import com.example.sad.lineChart.model.LineType
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeviceDataScreen(navController: NavController, deviceId: Int) {
    val context = LocalContext.current.applicationContext
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(token, context))
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

    val chartTabs = listOf("Temperature", "Humidity")
    var selectedChartTabIndex by remember { mutableStateOf(0)}

    var shownChart by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(LocalDate.now())}

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
            Box(
                contentAlignment = Alignment.TopCenter,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                if (shownChart)
                {
                    var yAxisValues = Array<Float>(48) { 0f }
                    var fullXAxisRange = generateSequence(0.0) { it + 0.5 }.takeWhile { it <= 24.0 }.map { String.format("%.0f", it) }.toList()

                    measurements
                        .filter { it.type_name == chartTabs[selectedChartTabIndex] && isSameDay(it.timestamp, selectedDate) }
                        .forEach { measurement ->
                            // Parse the timestamp to get the hour and minute
                            val zoneId = ZoneId.systemDefault()
                            val zonedDateTime = ZonedDateTime.parse(measurement.timestamp).withZoneSameInstant(zoneId)
                            val hour = zonedDateTime.hour
                            val minute = zonedDateTime.minute

                            // Calculate the index for the yAxisValues based on the hour and minute
                            val index = hour * 2 + (minute / 30) // Each hour has 2 half-hour segments

                            // Set the corresponding value in the yAxisValues array
                            yAxisValues[index] = measurement.value
                        }

                    yAxisValues = removeTrailingZeros(yAxisValues)

                    val startHour = getStartingHour(yAxisValues)
                    if (startHour > 0){
                        fullXAxisRange = fullXAxisRange.slice(startHour-1..<fullXAxisRange.size)
                        fullXAxisRange = generateSequence(startHour.toDouble()/2) { it + 0.5 }.takeWhile { it < 24.0 }.map { String.format("%.0f", it) }.toList()
                        yAxisValues = removeStartingZeros(yAxisValues)
                    }

                    PopupBox(
                        selectedDate = selectedDate,
                        selectedChartTabIndex = selectedChartTabIndex,
                        chartTabs = chartTabs,
                        onDateChanged = { newDate ->
                            if (newDate != LocalDate.now().plusDays(1)){
                                selectedDate = newDate
                            }
                        },
                        onClickOutside = { shownChart = false},
                        onTabChange = { selectedChartTabIndex = it }
                    ) {
                        Chart(xAxis = fullXAxisRange, yAxis = yAxisValues.toList(), yName = chartTabs[selectedChartTabIndex], selectedDate = selectedDate)
                    }
                }
                MeasurementTable(measurements = measurements, measurementsListState) { date ->
                    shownChart = true
                    selectedDate = date
                }
            }
            if (isTopOfList){
                PullRefreshIndicator(refreshing = viewModel.isRefreshing, state = pullRefreshState)
            }
        }
    }
}

fun removeTrailingZeros(data: Array<Float>): Array<Float> {
    var endIndex = data.size - 1
    while (endIndex >= 0 && data[endIndex] == 0f) {
        endIndex--
    }
    return data.slice(0..endIndex).toTypedArray()
}

fun removeStartingZeros(data: Array<Float>): Array<Float>{
    var startIndex = 0
    while(startIndex < data.size-1 && data[startIndex] == 0f){
        startIndex++
    }
    return data.slice(startIndex..<data.size).toTypedArray()
}

fun getStartingHour(data: Array<Float>): Int {
    // returns value from 0 to 47 (every 1/2 hour period)
    var startIndex = 0
    while(startIndex < data.size-1 && data[startIndex] == 0f){
        startIndex++
    }
    Log.d("START HOUR", "VALUE: $startIndex")
    return startIndex
}

fun isSameDay(timestamp:String, selectedDate: LocalDate): Boolean{
    val zonedDateTime = ZonedDateTime.parse(timestamp).withZoneSameInstant(ZoneId.systemDefault())
    val dateFromTimestamp = zonedDateTime.toLocalDate()
    return dateFromTimestamp == selectedDate
}


@Composable
fun MeasurementTable(measurements: List<Measurement>, state: LazyListState, onChartClick: (LocalDate) -> Unit ) {


    val distinctTypes = measurements.distinctBy { it.type }

    val groupedByDate = measurements.groupBy {
        val zonedDateTime = ZonedDateTime.parse(it.timestamp).withZoneSameInstant(ZoneId.systemDefault())
        zonedDateTime.toLocalDate()
    }

    val expandedStates = remember { mutableStateMapOf<LocalDate, Boolean>() }

    LazyColumn(state = state)
    {
        groupedByDate.forEach { (date, dailyMeasurements) ->
            item {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 5.dp)
                        .background(color = MaterialTheme.colorScheme.secondary)
                        .clickable {
                            expandedStates[date] = !(expandedStates[date] ?: false)
                        }
                ){
                    // Center the date
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Text(
                            text = date.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                    // Arrow button on the right edge
                    IconButton(onClick = {
                        onChartClick(date)
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.line_chart_icon),
                            contentDescription = "Chart",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            if (expandedStates[date] == true) {
                dailyMeasurements.chunked(distinctTypes.size).withIndex().forEach { (index, measurementGroup) ->
                    item {
                        MeasurementRow(
                            measurementGroup = measurementGroup,
                            textColor = if (index % 2 == 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onTertiaryContainer,
                            backgroundColor = if (index % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.tertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PopupBox(
    selectedDate: LocalDate,
    selectedChartTabIndex: Int,
    chartTabs: List<String>,
    onTabChange: (Int) -> Unit,
    onDateChanged: (LocalDate) -> Unit,
    onClickOutside: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
            .zIndex(10F),
        contentAlignment = Alignment.Center
    ) {
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(
                excludeFromSystemGesture = true,
            ),
            onDismissRequest = { onClickOutside() }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.7f)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp), // Padding for the content inside the popup
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween, // Ensure date selector is at the bottom
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    TabRow(
                        selectedTabIndex = selectedChartTabIndex,
                        containerColor = MaterialTheme.colorScheme.surface,
                        indicator = { tabPositions ->
                            // Draw a custom indicator below the selected tab
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[selectedChartTabIndex]) // Position the indicator
                                    .background(MaterialTheme.colorScheme.tertiary, RoundedCornerShape(4.dp)) // Change color here
                                    .height(3.dp) // Set height of the indicator
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape = RoundedCornerShape(12.dp))
                            .padding(bottom = 12.dp)
                    ) {
                        chartTabs.forEachIndexed { index, title ->
                            Tab(
                                selected = index == selectedChartTabIndex,
                                onClick = { onTabChange(index) },
                                text = {
                                    Text(
                                        text = title,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.W600
                                    )
                                }
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f) // Let the content take most of the vertical space
                            .fillMaxWidth()
                            .padding(8.dp)
                    ) {
                        content()
                    }
                    // Date selector at the bottom of the popup
                    DateSelector(
                        selectedDate = selectedDate,
                        onDateChanged = onDateChanged
                    )
                }
            }
        }
    }
}

@Composable
fun Chart(xAxis: List<String>, yAxis: List<Float>, yName: String, selectedDate: LocalDate) {

    // Create a list of LineParameters for each yValue
    val parameters: List<LineParameters> = listOf(
        LineParameters(
            label = yName,
            data = yAxis.map { it.toDouble() },
            lineColor = MaterialTheme.colorScheme.tertiary,
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        )
    )

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        LineChart(
            modifier = Modifier
                .fillMaxSize(),
            linesParameters = parameters,
            isGrid = true,
            gridColor = MaterialTheme.colorScheme.onSurface,
            xAxisData = xAxis,
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            xAxisStyle = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            descriptionStyle = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurface
            ),
            yAxisRange = 10,
            oneLineChart = false,
            gridOrientation = GridOrientation.GRID,
            drawEveryN = if (xAxis.size < 24) 2 else (xAxis.count() / 12),
//            drawEveryN = (xAxis.count() / 12),
//            drawEveryN = 2,
            date = selectedDate
        )
    }
}

@Composable
fun DateSelector(
    selectedDate: LocalDate,
    onDateChanged: (LocalDate) -> Unit,
){
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // Decrement day button
        IconButton(onClick = {
            onDateChanged(selectedDate.minusDays(1))
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Previous Day"
            )
        }

        // Display selected date in the format "23 Oct"
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM")),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Increment day button
        IconButton(onClick = {
            onDateChanged(selectedDate.plusDays(1))
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                tint = MaterialTheme.colorScheme.onSurface,
                contentDescription = "Next Day"
            )
        }
    }
}

@Composable
fun MeasurementRow(
    measurementGroup: List<Measurement>,
    textColor: Color,
    backgroundColor: Color
) {
    val zoneId = ZoneId.systemDefault()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val zonedDateTime = ZonedDateTime.parse(measurementGroup[0].timestamp).withZoneSameInstant(zoneId)
    val formattedTimestamp = formatter.format(zonedDateTime)

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
//            .border(1.dp, MaterialTheme.colorScheme.onBackground)
            .padding(horizontal = 8.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.time_icon),
                contentDescription = "Time",
                modifier = Modifier.size(24.dp),
                tint = textColor
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = formattedTimestamp,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor
            )
        }
        measurementGroup.sortedBy { it.type }.forEach { measurement ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ){
                Icon(
                    painter = painterResource(
                        id = if (measurement.type_name == "Temperature") {
                                R.drawable.temperature_icon
                            } else if (measurement.type_name == "Humidity") {
                                R.drawable.humidity_icon
                            } else {
                            R.drawable.data_24
                        }
                        ),
                    contentDescription = "Temperature",
                    modifier = Modifier.size(24.dp),
                    tint = textColor
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${measurement.value} ${getMeasurementUnitSign(measurement)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = textColor
                )
            }
        }
    }
}


fun getMeasurementUnitSign(measurement: Measurement): String{
    return when (measurement.unit) {
        "celsius" -> {
            "°C"
        }
        "percent" -> {
            "%"
        }
        "fahrenheit" -> {
            "°F"
        }
        else -> {
            "${measurement.unit}"
        }
    }
}