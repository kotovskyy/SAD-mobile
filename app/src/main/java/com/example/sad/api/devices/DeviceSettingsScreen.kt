package com.example.sad.api.devices

import android.app.TimePickerDialog
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sad.HomeActivity.DeviceSetting
import com.example.sad.HomeActivity.DevicesViewModel
import com.example.sad.HomeActivity.DevicesViewModelFactory
import com.example.sad.HomeActivity.HomeTopBar
import com.example.sad.LoginSignup.BottomNavItem
import com.example.sad.LoginSignup.BottomNavigationBar
import com.example.sad.R
import com.example.sad.SADApplication
import com.example.sad.api.auth.SecureStorage
import com.example.sad.navigateSingleOnTop


@Composable
fun DeviceSettingsScreen(navController: NavController, deviceId: Int) {
    val context = LocalContext.current.applicationContext
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(token, context))
    val device = viewModel.devices.collectAsState().value.find { it.id == deviceId }

    LaunchedEffect(deviceId) {
        viewModel.fetchDeviceSettings(deviceId)
    }
    val settings = viewModel.deviceSettings.collectAsState().value

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

    Scaffold(
        topBar = { HomeTopBar("Device settings") },
        bottomBar = { BottomNavigationBar(navController, "device_settings/$deviceId", bottomNavItems) }
    ){ innerPadding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SettingsForm(settings, updateSettings = {
                navController.navigateSingleOnTop("device_data/$deviceId")
                settings.forEach() { setting ->
                    viewModel.updateDeviceSetting(setting.id)
                }
            },
                updateSettingValue = { id, value ->
                    viewModel.updateSettingValue(id, value)
                }
            )
        }
    }
}

@Composable
fun SettingsForm(settings: List<DeviceSetting>, updateSettings: () -> Unit, updateSettingValue: (Int, Float) -> Unit){
    val context = LocalContext.current.applicationContext
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(ScrollState(0))
    ) {
        settings.forEach(){ setting ->
            var text by remember { mutableStateOf(setting.value.toString()) }
            Spacer(modifier = Modifier.height(8.dp))
            if (setting.type == 1 || setting.type == 2){
                TimeSetting(setting, updateSettingValue)
            } else {
                OutlinedTextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        if (newText.isNotEmpty()){
                            newText.toIntOrNull()?.let { validInt ->
                                updateSettingValue(setting.id, validInt.toFloat())
                            }
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                    Text(text = "${setting.type_name} (${setting.unit})", style = MaterialTheme.typography.labelLarge)
                })
            }
        }
        Button(
            onClick = {
                Toast.makeText(context, "Settings saved!", Toast.LENGTH_SHORT).show()
                updateSettings()
            },
            modifier = Modifier
                .padding(8.dp)
                .clip(shape = RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary)
        ){
            Text("Save", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
fun TimeSetting(setting: DeviceSetting, updateSettingValue: (Int, Float) -> Unit) {
    val context = LocalContext.current

    fun formatTime(timeValue: Float): String {
        val hours = (timeValue.toInt() / 100).toString().padStart(2, '0')
        val minutes = (timeValue.toInt() % 100).toString().padStart(2, '0')
        return "$hours:$minutes"
    }

    var timeDisplay by remember { mutableStateOf(formatTime(setting.value)) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OutlinedTextField(
            value = timeDisplay,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(text = setting.type_name, style = MaterialTheme.typography.labelLarge)
            },
            trailingIcon = {
                IconButton(onClick = {
                    val hours = setting.value.toInt() / 100
                    val minutes = setting.value.toInt() % 100
                    TimePickerDialog(context, { _: TimePicker, hourOfDay: Int, minute: Int ->
                        val newTimeValue = hourOfDay * 100 + minute
                        if (newTimeValue.toFloat() != setting.value) {
                            updateSettingValue(setting.id, newTimeValue.toFloat())
                            timeDisplay = formatTime(newTimeValue.toFloat())
                        }
                    }, hours, minutes, true).show()
                }) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit Time",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}