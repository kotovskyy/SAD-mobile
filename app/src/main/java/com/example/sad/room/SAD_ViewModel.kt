package com.example.sad.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.sad.room.Devices.Device
import com.example.sad.room.Measurements.Measurement
import kotlinx.coroutines.launch

class SAD_ViewModel (private val repository: SAD_Repository) : ViewModel() {
//    val allMeasurements: LiveData<List<Measurement>> = repository.getAllMeasurementsStream().asLiveData()
    val allDevices: LiveData<List<Device>> = repository.getAllDevicesStream().asLiveData()

    fun fetchMeasurementsFromServer() {
        viewModelScope.launch {
            val newMeasurements = fetchFromServer()
            repository.insertMeasurements(newMeasurements)
        }
    }

    private suspend fun fetchFromServer(): List<Measurement> {
        return listOf(
            Measurement(1L, System.currentTimeMillis(), 23.4f, 1, 1),
            Measurement(2L, System.currentTimeMillis(), 23.4f, 1, 1)
        )
    }
}

class MeasurementViewModelFactory(private val repository: SAD_Repository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SAD_ViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SAD_ViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}