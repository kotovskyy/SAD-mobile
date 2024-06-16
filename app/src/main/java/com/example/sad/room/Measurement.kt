package com.example.sad.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "measurements")
data class Measurement(
    @PrimaryKey(autoGenerate = false)
    var id: Long,
    var timestamp: Long,
    var value: Float,
    var device: Long,
    var type: Int
)
