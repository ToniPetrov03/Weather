package com.example.weather.utils

import android.content.Context
import com.example.weather.R

fun mapWindSpeedToText(context: Context, speed: Double?): String {
    val roundedSpeed = speed?.toInt()

    val windDescription = when {
        roundedSpeed == null -> context.getString(R.string.wind)
        roundedSpeed < 1 -> context.getString(R.string.calm)
        roundedSpeed < 2 -> context.getString(R.string.light_air)
        roundedSpeed < 4 -> context.getString(R.string.light_breeze)
        roundedSpeed < 6 -> context.getString(R.string.gentle_breeze)
        roundedSpeed < 8 -> context.getString(R.string.moderate_breeze)
        roundedSpeed < 11 -> context.getString(R.string.fresh_breeze)
        roundedSpeed < 14 -> context.getString(R.string.strong_gale)
        roundedSpeed < 18 -> context.getString(R.string.whole_gale)
        roundedSpeed < 21 -> context.getString(R.string.storm)
        else -> context.getString(R.string.violent_storm)
    }

    return context.getString(R.string.wind_speed, windDescription, roundedSpeed)
}
