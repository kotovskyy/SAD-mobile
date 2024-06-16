package com.example.sad.api.devices

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.sad.HomeActivity.DeviceSetting
import com.example.sad.HomeActivity.DevicesViewModel
import com.example.sad.HomeActivity.DevicesViewModelFactory
import com.example.sad.HomeActivity.HomeTopBar
import com.example.sad.LoginSignup.BottomNavItem
import com.example.sad.R
import com.example.sad.api.auth.SecureStorage
import com.example.sad.navigateSingleOnTop


@Composable
fun DeviceSettingsScreen(navController: NavController, deviceId: Int) {
    val context = LocalContext.current
    val token = SecureStorage.getToken(context)
    val viewModel: DevicesViewModel = viewModel(factory = DevicesViewModelFactory(token))
    val device = viewModel.devices.collectAsState().value.find { it.id == deviceId }

    LaunchedEffect(deviceId) {
        viewModel.fetchDeviceSettings(deviceId)
    }
    val settings = viewModel.deviceSettings.collectAsState().value

    Scaffold(
        topBar = { HomeTopBar("Device settings") },
        bottomBar = { DeviceBottomNavigationBar(navController, "device_settings/$deviceId", deviceId) }
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
            if (setting.type == 1 || setting.type == 2){
                TimeSetting(setting, updateSettingValue)
            } else {
                OutlinedTextField(
                    value = text,
                    onValueChange = { newText ->
                        text = newText
                        newText.toFloatOrNull()?.let { validFloat ->
                            updateSettingValue(setting.id, validFloat)
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = {
                    Text(text = setting.typeDescription)
                })
            }
//            Text(text = "${setting.id} | ${setting.typeDescription} | ${setting.value}")
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSetting(setting: DeviceSetting, updateSettingValue: (Int, Float) -> Unit) {
    val hours = (setting.value / 100).toInt()
    val minutes = (setting.value % 100).toInt()
    val timePickerState = rememberTimePickerState(initialHour = hours, initialMinute = minutes)

    LaunchedEffect(timePickerState.hour, timePickerState.minute) {
        val newTimeValue = timePickerState.hour * 100 + timePickerState.minute
        if (newTimeValue.toFloat() != setting.value) {
            updateSettingValue(setting.id, newTimeValue.toFloat())
        }
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Text(setting.typeDescription, style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(7.dp))
        TimeInput(state = timePickerState)
    }
}