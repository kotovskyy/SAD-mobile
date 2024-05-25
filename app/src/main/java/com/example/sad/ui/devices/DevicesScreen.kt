package com.example.sad.ui.devices

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.sad.ui.onboarding.HeaderBox
import com.example.sad.ui.onboarding.OutlinedPrimaryButton
import com.example.sad.ui.theme.SADTheme
import com.example.sad.ui.viewModels.DevicesViewModel
import java.security.Permission
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sad.ConnectionThread
import java.io.IOException
import java.util.UUID


@Composable
fun DevicesScreen(navController: NavController, viewModel: DevicesViewModel = viewModel()) {
    val context = LocalContext.current
    var deviceName by remember { mutableStateOf("No device connected") }
    val devices = viewModel.getDevices()
//    val uuid = viewModel.getUUID()
    val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    val bluetoothAdapter by remember { mutableStateOf(bluetoothManager.adapter) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        HeaderBox(
            mainText = "Your devices",
            secondaryText = "",
            dividerThickness = (-1).dp,
        )
        Spacer(modifier = Modifier.height(0.dp))
        AddNewDevice(context, bluetoothAdapter, addDevice = { name, address ->
            viewModel.addDevice(name, address)
        })
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = deviceName)
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ){
            devices.forEach {
                Text(
                    text = "${it.name} - ${it.address}",
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(horizontal = 10.dp, vertical = 15.dp)
                        .fillMaxWidth()
                        .clickable {
                            Toast.makeText(context, "Clicked ${it.name}", Toast.LENGTH_LONG).show()

                            var device: BluetoothDevice = bluetoothAdapter.getRemoteDevice(it.address)
                            var connectionThread = ConnectionThread(device, context, bluetoothAdapter)
                            connectionThread.start()
                            Toast.makeText(context, "Trying to connect", Toast.LENGTH_LONG).show()
                        }
                )
            }
        }
    }
}


@Composable
fun AddNewDevice(context: Context, bluetoothAdapter: BluetoothAdapter, addDevice: (String, String) -> Unit) {

    val requiredPermissions = arrayOf(
        Manifest.permission.BLUETOOTH,
        Manifest.permission.BLUETOOTH_ADMIN,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
        )

    val enableBluetoothLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Bluetooth enabled, now request device association
            Toast.makeText(context, result.data.toString(), Toast.LENGTH_LONG).show()
        }
    }

    val requestPermissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (requiredPermissions.all { permissions[it] == true }) {
            Toast.makeText(context, "Permissions granted", Toast.LENGTH_LONG).show()
        } else {
            var counter = 0
            for (permission in requiredPermissions){
                if (permissions[permission] == false) {
                    counter++
                }
            }
            Toast.makeText(context, "Some permissions not granted: $counter", Toast.LENGTH_LONG).show()
        }
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        OutlinedPrimaryButton(
            text = "Add new device",
            onClick = {
                if (requiredPermissions.all {
                    ActivityCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
                })
                {
                    requestPermissionsLauncher.launch(requiredPermissions)
                }
                else {
                    if (bluetoothAdapter == null) {
                        // Device doesn't support Bluetooth
                        return@OutlinedPrimaryButton
                    }
                    if (!bluetoothAdapter.isEnabled) {
                        // Bluetooth is not enabled, request to enable it
                        val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                        enableBluetoothLauncher.launch(enableBtIntent)
                    } else {
                        // Bluetooth is already enabled, request device association
                        val intent = Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS)
                        Toast.makeText(context, "Starting device discovery", Toast.LENGTH_LONG).show()
//                        context.startActivity(intent)
                        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter.bondedDevices
                        pairedDevices?.forEach { device ->
                            val deviceName = device.name
                            val deviceAddress = device.address
                            val deviceUUID = if (device.uuids != null) {
                                device.uuids[0].uuid
                            } else { null }
                            Log.e("DEVICE UUID", "${deviceName} : ${deviceUUID}")
                            addDevice(deviceName, deviceAddress)
                        }

                    }
                }
            }
        )
    }
}


@Preview(showSystemUi = true)
@Composable
fun DevicesScreenPreview() {
    SADTheme {
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            val navController = rememberNavController()
            DevicesScreen(navController = navController)
        }
    }
}