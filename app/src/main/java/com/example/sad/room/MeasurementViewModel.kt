package com.example.sad.room

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.math.BigInteger
import java.sql.Timestamp

class MeasurementViewModel (private val repository: MeasurementsRepository) : ViewModel() {
    val allMeasurements: LiveData<List<Measurement>> = repository.getAllMeasurementsStream().asLiveData()

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

class MeasurementViewModelFactory(private val repository: MeasurementsRepository) : ViewModelProvider.Factory {
    override fun <T: ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MeasurementViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MeasurementViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}