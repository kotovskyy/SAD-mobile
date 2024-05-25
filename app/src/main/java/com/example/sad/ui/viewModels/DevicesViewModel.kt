package com.example.sad.ui.viewModels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import java.util.UUID


data class Device(var name: String, val address: String)

class DevicesViewModel : ViewModel() {
    private var _devices = mutableStateListOf<Device>()
    private var _uuid = UUID.randomUUID()

    fun getUUID() : UUID {
        return _uuid
    }

    fun addDevice(name: String, address: String) {
        _devices.add(Device(name, address))
    }

    fun getDevices() : Set<Device> {
        return _devices.toSet()
    }
}