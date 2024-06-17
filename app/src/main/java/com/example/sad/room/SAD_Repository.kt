package com.example.sad.room
import com.example.sad.room.Devices.Device
import com.example.sad.room.Measurements.Measurement
import kotlinx.coroutines.flow.Flow


interface SAD_Repository {
    fun getAllMeasurementsStream(deviceId: Int): Flow<List<Measurement>>
    suspend fun insertMeasurements(measurements: List<Measurement>)

    suspend fun insertDevices(device: List<Device>)
    fun getAllDevicesStream(): Flow<List<Device>>

    suspend fun clearAllData()
}