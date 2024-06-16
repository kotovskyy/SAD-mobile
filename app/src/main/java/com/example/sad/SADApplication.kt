package com.example.sad

import android.app.Application
import androidx.room.Database
import com.example.sad.room.Offline_SAD_Repository
import com.example.sad.room.SAD_DB

class SADApplication : Application() {
    val db by lazy { SAD_DB.getDB(this) }
    val repository by lazy { Offline_SAD_Repository(db.getMeasurementDao(), db.getDeviceDao()) }
}
