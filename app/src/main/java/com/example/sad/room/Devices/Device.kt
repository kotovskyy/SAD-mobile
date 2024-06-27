package com.example.sad.room.Devices

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "devices")
data class Device(
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var name: String,
    var mac_address: String,
    var user: Long,
    var type: Int
)