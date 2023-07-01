package com.example.weather.utils

import com.example.weather.LANG
import com.example.weather.MILLISECONDS_PER_SECOND
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEEE, d MMMM", Locale(LANG))
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}

fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale(LANG))
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}
