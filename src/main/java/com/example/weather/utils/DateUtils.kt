package com.example.weather.utils

import com.example.weather.api.LANG
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

internal object DateUtils {

    private const val MILLISECONDS_PER_SECOND = 1000L

    fun formatDate(timestamp: Int): String {
        val sdf = SimpleDateFormat("EEEE, d MMMM", Locale.forLanguageTag(LANG))
        return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
    }

    fun formatTime(timestamp: Int): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.forLanguageTag(LANG))
        return sdf.format(Date(timestamp * MILLISECONDS_PER_SECOND))
    }
}
