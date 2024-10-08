package com.example.sad.room

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sad.room.Measurements.Measurement
import com.example.sad.room.Measurements.MeasurementDao
import com.example.sad.room.Devices.Device
import com.example.sad.room.Devices.DeviceDao



@Database(entities = [Measurement::class, Device::class], version = 3, exportSchema = false)
abstract class SAD_DB : RoomDatabase() {

    abstract fun getMeasurementDao() : MeasurementDao
    abstract fun getDeviceDao() : DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: SAD_DB? = null

        fun getDB(context: Context): SAD_DB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, SAD_DB::class.java, "sad_db")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
