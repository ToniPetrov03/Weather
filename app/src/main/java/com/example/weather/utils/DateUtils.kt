package com.example.weather.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale.getDefault

private const val MILLISECONDS_PER_SECOND = 1000L

fun formatDate(timestamp: Int): String {
    val sdf = SimpleDateFormat("EEEE, d MMMM", getDefault())
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}

fun formatTime(timestamp: Int): String {
    val sdf = SimpleDateFormat("HH:mm", getDefault())
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}

fun formatDateTime(timestamp: Int): String {
    val sdf = SimpleDateFormat("HH:mm, dd MMM", getDefault())
    return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
}
