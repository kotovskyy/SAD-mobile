package com.example.sad.room
import com.example.sad.room.Devices.Device
import com.example.sad.room.Devices.DeviceDao
import com.example.sad.room.Measurements.Measurement
import com.example.sad.room.Measurements.MeasurementDao
import kotlinx.coroutines.flow.Flow

class Offline_SAD_Repository(private val measurementDao: MeasurementDao, private val deviceDao: DeviceDao) :
    SAD_Repository {
    override fun getAllMeasurementsStream(): Flow<List<Measurement>> = measurementDao.getAll()
    override suspend fun insertMeasurements(measurements: List<Measurement>) {
        measurementDao.insertAll(measurements)
    }

    override fun getAllDevicesStream(): Flow<List<Device>> = deviceDao.getAll()
    override suspend fun insertDevices(devices: List<Device>) {
        deviceDao.insertAll(devices)
    }
}