package com.example.weather.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val LOCATIONS_PREFERENCE_FILE_NAME = "locations_preference"

data class Location(val name: String, val lat: Double, val lon: Double)

fun saveLocationPreferences(context: Context, locationList: List<Location>) {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(LOCATIONS_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
    val editor: SharedPreferences.Editor = sharedPreferences.edit()

    val json = Gson().toJson(locationList)
    editor.putString("location_list", json)
    editor.apply()
}

fun readLocationPreferences(context: Context): List<Location> {
    val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(LOCATIONS_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)
    val json = sharedPreferences.getString("location_list", null)
    val type = object : TypeToken<List<Location>>() {}.type

    return Gson().fromJson(json, type) ?: emptyList()
}

fun addLocationPreferences(context: Context, location: Location) {
    val locationList = readLocationPreferences(context).toMutableList()
    locationList.add(location)
    saveLocationPreferences(context, locationList)
}

fun removeLocationPreference(context: Context, index: Int) {
    val locationList = readLocationPreferences(context).toMutableList()
    if (index in 0 until locationList.size) {
        locationList.removeAt(index)
        saveLocationPreferences(context, locationList)
    }
}
