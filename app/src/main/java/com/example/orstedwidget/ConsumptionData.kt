package com.example.orstedwidget

/**
 * Data class for the consumption
 */
data class ConsumptionData (
    val start: String,
    val end: String,
    val kWh: Double,
    val COPI: HashMap<String, String>
)