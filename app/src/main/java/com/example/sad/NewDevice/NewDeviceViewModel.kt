package com.example.sad.NewDevice

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WifiNetworkViewModel(applicationContext: Context) : ViewModel() {


    private val _wifiSSID = MutableStateFlow<String?>(null)

    private val context = applicationContext
    val wifiSSID = _wifiSSID.asStateFlow()


    private fun checkPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
    }

    fun fetchCurrentWifiSSID() {
        viewModelScope.launch {
            _wifiSSID.value = getCurrentWifiSSID()
        }
    }

    private fun getCurrentWifiSSID(): String {
        if (!checkPermissions(context)) {
            Log.d("WifiNetworkViewModel", "Location permission not granted")
            return "<Permissions not granted>"
        }

        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val info = wifiManager.connectionInfo
        Log.d("WifiNetworkViewModel", "SSID: ${info.ssid}")
        Log.d("WifiNetworkViewModel", "MAC: ${info.macAddress}")

        return if (info.networkId == -1) {
            "<Not connected to WiFi>"
        } else {
            info.ssid.trim('"') // Strip quotes from SSID if present
        }
    }
}