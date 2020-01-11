package com.example.orstedwidget

data class DailyData(
    val CustomerNumber: String,
    val data: List<Map<String, String>>,
    val readings: List<String>,
    val COPI: Map<String, String>
)