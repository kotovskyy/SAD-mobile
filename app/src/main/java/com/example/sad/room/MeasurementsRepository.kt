package com.example.sad.room
import kotlinx.coroutines.flow.Flow


interface MeasurementsRepository {
    fun getAllMeasurementsStream(): Flow<List<Measurement>>
    suspend fun insertMeasurements(measurements: List<Measurement>)
}