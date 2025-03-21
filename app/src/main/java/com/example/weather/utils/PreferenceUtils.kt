package com.example.weather.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.core.content.edit
import com.example.weather.models.Location

const val WEATHER_PREFERENCE_FILE_NAME = "weather_preference"
const val PROP_LOCATIONS = "locations"

inline fun <reified T> jsonParse(json: String): T =
    Gson().fromJson(json, object : TypeToken<T>() {}.type)

private fun getPreferences(context: Context): SharedPreferences =
    context.getSharedPreferences(WEATHER_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

private fun SharedPreferences.getLocationsPreference(): List<Location> =
    getString(PROP_LOCATIONS, null)?.let { jsonParse(it) } ?: listOf()

private fun SharedPreferences.updateLocationPreference(locations: List<Location>) = edit {
    putString(PROP_LOCATIONS, Gson().toJson(locations))
}

fun getLocationsPreference(context: Context) = getPreferences(context).getLocationsPreference()

fun addLocationPreference(context: Context, value: Location) = getPreferences(context).apply {
    updateLocationPreference(getLocationsPreference() + value)
}

fun removeLocationPreference(context: Context, value: Location) = getPreferences(context).apply {
    updateLocationPreference(getLocationsPreference() - value)
}
