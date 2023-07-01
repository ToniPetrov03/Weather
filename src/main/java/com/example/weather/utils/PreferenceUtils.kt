package com.example.weather.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import androidx.core.content.edit
import com.example.weather.utils.Utils.jsonParse
import org.osmdroid.util.GeoPoint

internal object PreferenceUtils {

    private const val WEATHER_PREFERENCE_FILE_NAME = "weather_preferences"
    private const val PREFERENCE_LOCATIONS = "locations"

    private fun getPreferences(context: Context): SharedPreferences =
        context.getSharedPreferences(WEATHER_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

    private fun SharedPreferences.getLocationsPreference(): List<GeoPoint> =
        getString(PREFERENCE_LOCATIONS, null)?.let { jsonParse(it) } ?: listOf()

    private fun SharedPreferences.updateLocationPreference(locations: List<GeoPoint>) = edit {
        putString(PREFERENCE_LOCATIONS, Gson().toJson(locations))
    }

    fun getLocationsPreference(context: Context) = getPreferences(context).getLocationsPreference()

    fun addLocationPreference(context: Context, value: GeoPoint) = getPreferences(context).apply {
        val locations = getLocationsPreference()
        val hasLocation = locations.any {
            it.latitude == value.latitude && it.longitude == value.longitude
        }
        if (!hasLocation) updateLocationPreference(locations + value)
    }

    fun removeLocationPreference(context: Context, value: GeoPoint) = getPreferences(context).apply {
        updateLocationPreference(getLocationsPreference() - value)
    }
}
