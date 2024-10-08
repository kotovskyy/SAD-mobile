package com.example.sad.room.Measurements

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var timestamp: Long,
    var value: Float,
    var device: Long,
    var type: Int,
    var type_name: String,
    var unit: String
)
