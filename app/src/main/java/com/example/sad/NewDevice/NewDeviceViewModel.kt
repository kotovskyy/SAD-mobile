package com.example.sad.NewDevice

import android.app.Application
import android.content.Context
import android.net.wifi.WifiManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class NewDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val _currentSSID = MutableLiveData<String>()
    val currentSSID: LiveData<String> = _currentSSID

    fun fetchCurrentSSID() {
        val wifiManager = getApplication<Application>().getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiInfo = wifiManager.connectionInfo
        if (wifiInfo.networkId != -1) {
            // Removing quotes that may be present around the SSID name on some devices
            _currentSSID.value = wifiInfo.ssid.removePrefix("\"").removeSuffix("\"")
        } else {
            _currentSSID.value = "Not connected"
        }
    }
}