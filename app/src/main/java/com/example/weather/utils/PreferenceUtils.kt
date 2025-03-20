package com.example.weather.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit
import com.example.weather.models.Location

const val WEATHER_PREFERENCE_FILE_NAME = "weather_preference"
const val PROP_LOCATIONS = "locations"

inline fun <reified T> jsonParse(json: String): T =
    Gson().fromJson(json, object : TypeToken<T>() {}.type)

private fun getPreferences(context: Context) =
    context.getSharedPreferences(WEATHER_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

fun getLocationsPreference(context: Context): List<Location> =
    getPreferences(context).getString(PROP_LOCATIONS, null)?.let { jsonParse(it) } ?: listOf()

fun addLocationPreference(context: Context, value: Location) {
    val locations = getLocationsPreference(context).toMutableList()

    locations.add(value)

    getPreferences(context).edit {
        putString(PROP_LOCATIONS, Gson().toJson(locations))
    }
}

fun removeLocationPreference(context: Context, value: Location) {
    val locations = getLocationsPreference(context).toMutableList()
    locations.removeIf { it == value }

    getPreferences(context).edit {
        putString(PROP_LOCATIONS, Gson().toJson(locations))
    }
}
