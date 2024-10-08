package com.example.sad.room.Devices

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import retrofit2.http.DELETE

@Dao
interface DeviceDao {

    @Query("SELECT * FROM devices ORDER BY id DESC")
    fun getAll(): Flow<List<Device>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(devices: List<Device>)

    @Query("DELETE FROM devices")
    suspend fun deleteAllDevices()
}