package com.example.sad.room
import kotlinx.coroutines.flow.Flow

class OfflineMeasurementsRepository(private val measurementDao: MeasurementDao) : MeasurementsRepository {
    override fun getAllMeasurementsStream(): Flow<List<Measurement>> = measurementDao.getAll()
    override suspend fun insertMeasurements(measurements: List<Measurement>) {
        measurementDao.insertAll(measurements)
    }
}