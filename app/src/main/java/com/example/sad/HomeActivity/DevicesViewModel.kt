package com.example.sad.HomeActivity

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.sad.api.auth.SecureStorage
import com.example.sad.api.devices.AuthInterceptor
import com.example.sad.api.devices.DevicesApiService
import com.example.sad.api.devices.DevicesResponse
import com.example.sad.api.devices.DevicesRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

data class Device(
    val id: Int,
    val name: String,
    val user: Int,
    val mac_address: String,
    val type: Int?
)

class DevicesViewModelFactory(private val token: String?) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DevicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DevicesViewModel(token) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class DevicesViewModel(token: String?) : ViewModel() {
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices  = _devices.asStateFlow()
    private var api: DevicesApiService? = null

    init {
        api = DevicesRetrofitInstance.createApi(token = token)
        fetchDevices()
    }

    private fun fetchDevices() {
        api?.getAllDevices()?.enqueue(object : retrofit2.Callback<List<Device>> {
            override fun onResponse(call: retrofit2.Call<List<Device>>, response: retrofit2.Response<List<Device>>) {
                if (response.isSuccessful) {
                    // Update StateFlow with the new list of devices
                    _devices.value = response.body() ?: emptyList()
                } else {
                    Log.e("Device Fetch", "Failed to fetch devices: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Device>>, t: Throwable) {
                Log.e("Device Fetch", "Network error: ${t.message}")
            }
        })
    }
}
