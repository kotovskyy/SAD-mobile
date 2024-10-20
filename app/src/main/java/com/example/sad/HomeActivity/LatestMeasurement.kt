package com.example.sad.HomeActivity

import java.sql.Timestamp

data class LatestMeasurement(
    val deviceID: Int,
    val temperatureValue: Float,
    val humidityValue: Float,
    val timestamp: String
)
