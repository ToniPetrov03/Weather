package com.example.weather.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object DateUtils {

    private const val MILLISECONDS_PER_SECOND = 1000L

    fun formatDate(timestamp: Int, locale: Locale): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM", locale)
        return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
    }

    fun formatTime(timestamp: Int, locale: Locale): String {
        val sdf = SimpleDateFormat("HH:mm", locale)
        return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
    }
}
