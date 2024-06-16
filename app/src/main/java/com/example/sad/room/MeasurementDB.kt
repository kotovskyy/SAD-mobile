package com.example.sad.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Measurement::class], version = 1, exportSchema = false)
abstract class MeasurementDB : RoomDatabase() {

    abstract fun getMeasurementDao() : MeasurementDao

    companion object {
        @Volatile
        private var INSTANCE: MeasurementDB? = null

        fun getDB(context: Context): MeasurementDB {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context, MeasurementDB::class.java, "measurement_db")
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}