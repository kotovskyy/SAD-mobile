package com.example.sad.HomeActivity

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sad.SADApplication
import com.example.sad.api.auth.SecureStorage
import com.example.sad.api.devices.AuthInterceptor
import com.example.sad.api.devices.DeleteDeviceResponse
import com.example.sad.api.devices.DevicesApiService
import com.example.sad.api.devices.DevicesRetrofitInstance
import com.example.sad.api.devices.SettingUpdateRequest
import com.example.sad.api.devices.SettingUpdateResponse
import com.example.sad.room.SAD_Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.sql.Timestamp
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class Device(
    val id: Int,
    val name: String,
    val user: Int,
    val mac_address: String,
    val type: Int
)

data class Measurement(
    val id: Int,
    val timestamp: String,
    val value: Float,
    val device: Int,
    val type: Int,
    val type_name: String,
    val unit: String
)

data class DeviceSetting(
    val id: Int,
    val type: Int,
    var value: Float,
    var type_name: String,
    var unit: String
)

fun parseTimestampToMillis(timestamp: String): Long {
    return Instant.parse(timestamp).toEpochMilli()
}

fun formatMillisToTimestamp(millis: Long): String {
    val instant = Instant.ofEpochMilli(millis)
    val formatter = DateTimeFormatter.ISO_INSTANT
    return formatter.format(instant)
}

fun convertApiDeviceToDbDevice(apiDevice: com.example.sad.HomeActivity.Device): com.example.sad.room.Devices.Device {
    return com.example.sad.room.Devices.Device(
        id = apiDevice.id.toLong(), // Convert Int to Long if necessary
        name = apiDevice.name,
        mac_address = apiDevice.mac_address,
        user = apiDevice.user.toLong(), // Convert Int to Long if necessary
        type = apiDevice.type // Handle nullable and conversion
    )
}

fun convertDbDeviceToApiDevice(dbDevice: com.example.sad.room.Devices.Device): com.example.sad.HomeActivity.Device {
    return com.example.sad.HomeActivity.Device(
        id = dbDevice.id.toInt(),  // Assuming your room uses Long and API uses Int
        name = dbDevice.name,
        mac_address = dbDevice.mac_address,
        user = dbDevice.user.toInt(),
        type = dbDevice.type  // Handle conversion with null safety
    )
}

// Convert from Network model to Room model
fun com.example.sad.HomeActivity.Measurement.toDbModel(): com.example.sad.room.Measurements.Measurement {
    return com.example.sad.room.Measurements.Measurement(
        id = this.id.toLong(),
        timestamp = parseTimestampToMillis(this.timestamp), // Assuming timestamp is a String you need to convert to Long
        value = this.value,
        device = this.device.toLong(),
        type = this.type,
        type_name = this.type_name,
        unit = this.unit
    )
}

// Convert from Room model to API model
fun com.example.sad.room.Measurements.Measurement.toApiModel(): com.example.sad.HomeActivity.Measurement {
    return com.example.sad.HomeActivity.Measurement(
        id = this.id.toInt(),
        timestamp = formatMillisToTimestamp(this.timestamp), // Converting back to String if necessary
        value = this.value,
        device = this.device.toInt(),
        type = this.type,
        type_name = this.type_name,
        unit = this.unit
    )
}

class DevicesViewModelFactory(private val token: String?, private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevicesViewModel::class.java)) {
            val repository = (context as SADApplication).repository
            return DevicesViewModel(token, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DevicesViewModel(
    token: String?,
    sadRepository: SAD_Repository
) : ViewModel() {
    private val repository: SAD_Repository = sadRepository
    private var api: DevicesApiService? = null
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    private val _deviceMeasurements = MutableStateFlow<List<Measurement>>(emptyList())
    private val _deviceSettings = MutableStateFlow<List<DeviceSetting>>(emptyList())

    val devices  = _devices.asStateFlow()
    val deviceMeasurements = _deviceMeasurements.asStateFlow()
    val deviceSettings = _deviceSettings.asStateFlow()
    var isRefreshing by mutableStateOf(false)


    init {
        api = DevicesRetrofitInstance.createApi(token = token)
    }

    fun updateSettingValue(id: Int, newValue: Float) {
        val updatedSettings = deviceSettings.value.map { setting ->
            if (setting.id == id) {
                setting.copy(value = newValue)
            } else {
                setting
            }
        }
        _deviceSettings.value = updatedSettings
    }

    fun fetchDevices() {
        isRefreshing = true
        api?.getAllDevices()?.enqueue(object : retrofit2.Callback<List<Device>> {
            override fun onResponse(call: retrofit2.Call<List<Device>>, response: retrofit2.Response<List<Device>>) {
                if (response.isSuccessful) {
                    // Update StateFlow with the new list of devices
                    _devices.value = response.body() ?: emptyList()
                    val dbDevices = _devices.value.map { device ->
                        convertApiDeviceToDbDevice(device)
                    }
                    viewModelScope.launch {
                        repository.insertDevices(dbDevices)
                        Log.d("INSERT DEVICES", "Successfully inserted devices")
                    }
                } else {
                    Log.e("Device Fetch", "Failed to fetch devices: ${response.errorBody()?.string()}")
                }
                isRefreshing = false
            }

            override fun onFailure(call: retrofit2.Call<List<Device>>, t: Throwable) {
                Log.e("Device Fetch", "Network error: ${t.message}")
                isRefreshing = false
            }
        })
        viewModelScope.launch {
            repository.getAllDevicesStream()
                .map { list ->
                    list.map { dbDevice ->
                        convertDbDeviceToApiDevice(dbDevice)
                    }
                }.collect { mappedList ->
                    _devices.value = mappedList
                }
        }
    }

    fun deleteDevice(deviceId: Int){
        api?.deleteDevice(deviceId)?.enqueue(object : retrofit2.Callback<DeleteDeviceResponse> {
            override fun onResponse(call: retrofit2.Call<DeleteDeviceResponse>, response: retrofit2.Response<DeleteDeviceResponse>) {
                if (response.isSuccessful) {
                    Log.e("Device delete", "Successfully deleted device")
                } else {
                    Log.e("Device Fetch", "Failed to fetch devices: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<DeleteDeviceResponse>, t: Throwable) {
                Log.e("Device delete", "Network error: ${t.message}")
            }
        })
    }

    fun fetchDeviceMeasurements(deviceId: Int) {
        isRefreshing = true
        api?.getDeviceMeasurements(deviceId)?.enqueue(object : retrofit2.Callback<List<Measurement>> {
            override fun onResponse(call: retrofit2.Call<List<Measurement>>, response: retrofit2.Response<List<Measurement>>) {
                if (response.isSuccessful) {
                    // Update StateFlow with the new list of devices
                    _deviceMeasurements.value = response.body() ?: emptyList()
                    _deviceMeasurements.value = _deviceMeasurements.value.sortedBy { m ->
                        m.timestamp
                    }
                    val measurements = _deviceMeasurements.value.map { it.toDbModel() }
                    viewModelScope.launch {
                        repository.insertMeasurements(measurements)
                        Log.d("INSERT MEASUREMENTS", "Successfully inserted measurements")
                    }
                } else {
                    Log.e("Device Fetch", "Failed to fetch devices: ${response.errorBody()?.string()}")
                }
                isRefreshing = false
            }

            override fun onFailure(call: retrofit2.Call<List<Measurement>>, t: Throwable) {
                Log.e("Device Fetch", "Network error: ${t.message}")
                isRefreshing = false
            }
        })
        viewModelScope.launch {
            repository.getAllMeasurementsStream(deviceId)
                .map { list ->
                    list.map { it.toApiModel() }
                }.collect { mappedList ->
                    _deviceMeasurements.value = mappedList
                }
        }
    }

    fun fetchDeviceSettings(deviceId: Int){
        api?.getDeviceSettings(deviceId)?.enqueue(object : retrofit2.Callback<List<DeviceSetting>> {
            override fun onResponse(call: retrofit2.Call<List<DeviceSetting>>, response: retrofit2.Response<List<DeviceSetting>>) {
                if (response.isSuccessful) {
                    // Update StateFlow with the new list of devices
                    _deviceSettings.value = response.body() ?: emptyList()
                    _deviceSettings.value = _deviceSettings.value.sortedBy { it.type }
                } else {
                    Log.e("Device Fetch", "Failed to fetch devices: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<DeviceSetting>>, t: Throwable) {
                Log.e("Device Fetch", "Network error: ${t.message}")
            }
        })
    }

    fun updateDeviceSetting(settingId: Int) {
        val value = deviceSettings.value.find { it.id == settingId }?.value
        val request = SettingUpdateRequest(value = value)
        api?.updateDeviceSetting(settingId, request)?.enqueue(object : retrofit2.Callback<SettingUpdateResponse> {
            override fun onResponse(call: retrofit2.Call<SettingUpdateResponse>, response: retrofit2.Response<SettingUpdateResponse>) {
                if (response.isSuccessful) {
                    Log.d("SETTING UPDATE", "Updated successfully, id=$settingId")
                } else {
                    Log.e("SETTING UPDATE", "Failed to update setting: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<SettingUpdateResponse>, t: Throwable) {
                Log.e("SETTING UPDATE", "Network error: ${t.message}")
            }
        })
    }

}
