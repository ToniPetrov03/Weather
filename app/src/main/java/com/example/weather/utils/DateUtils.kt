package com.example.weather.utils

import com.example.weather.api.LANG
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val MILLISECONDS_PER_SECOND = 1000L

fun formatDate(timestamp: Int): String {
    val sdf = SimpleDateFormat("EEEE, d MMMM", Locale(LANG))
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}

fun formatTime(timestamp: Int): String {
    val sdf = SimpleDateFormat("HH:mm", Locale(LANG))
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}

fun formatDateTime(timestamp: Int): String {
    val sdf = SimpleDateFormat("HH:mm, dd MMM", Locale(LANG))
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}
