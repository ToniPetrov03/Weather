package com.example.weather.utils

import android.content.Context
import com.example.weather.network.models.CurrentWeather
import com.example.weather.network.models.FutureWeather
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val WEATHER_PREFERENCE_FILE_NAME = "weather_preference"

data class WeatherData(
    val name: String,
    val lat: Double,
    val lon: Double,
    var currentWeather: CurrentWeather?,
    var futureWeather: List<FutureWeather>?
)

private inline fun <reified T> jsonParse(json: String): T =
    Gson().fromJson(json, object : TypeToken<T>() {}.type)

private fun getPreferences(c: Context) =
    c.getSharedPreferences(WEATHER_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

private fun savePreference(c: Context, value: LinkedHashMap<String, WeatherData>) =
    getPreferences(c)
        .edit()
        .putString("data", Gson().toJson(value))
        .apply()

private fun updateWeatherPreference(
    c: Context,
    lat: Double,
    lon: Double,
    newCurrentWeather: CurrentWeather?,
    newFutureWeather: List<FutureWeather>?
) {
    val weathersData = getWeathersDataPreference(c)

    weathersData["$lat/$lon"]?.apply {
        newCurrentWeather?.let { currentWeather = it }
        newFutureWeather?.let { futureWeather = it }
    }

    savePreference(c, weathersData)
}

fun updateWeatherPreference(c: Context, lat: Double, lon: Double, weather: CurrentWeather) =
    updateWeatherPreference(c, lat, lon, weather, null)

fun updateWeatherPreference(c: Context, lat: Double, lon: Double, weather: List<FutureWeather>) =
    updateWeatherPreference(c, lat, lon, null, weather)

fun getWeathersDataPreference(c: Context): LinkedHashMap<String, WeatherData> =
    getPreferences(c).getString("data", null)?.let { jsonParse(it) } ?: LinkedHashMap()

fun addLocationPreference(c: Context, name: String, lat: Double, lon: Double) {
    val weathersData = getWeathersDataPreference(c)

    weathersData["$lat/$lon"] = WeatherData(name, lat, lon, null, null)

    savePreference(c, weathersData)
}

fun removeLocationPreference(c: Context, weatherData: WeatherData) {
    val weathersData = getWeathersDataPreference(c)

    weathersData.remove("${weatherData.lat}/${weatherData.lon}")

    savePreference(c, weathersData)
}
