package com.example.weather.utils

import android.content.Context
import com.example.weather.current_weather.CurrentWeather
import com.example.weather.future_weather.FutureWeather
import com.example.weather.models.WeatherData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

const val WEATHER_PREFERENCE_FILE_NAME = "weather_preference"

inline fun <reified T> jsonParse(json: String): T =
    Gson().fromJson(json, object : TypeToken<T>() {}.type)

private fun getPreferences(c: Context) =
    c.getSharedPreferences(WEATHER_PREFERENCE_FILE_NAME, Context.MODE_PRIVATE)

private fun savePreference(c: Context, value: LinkedHashMap<String, WeatherData>) =
    getPreferences(c)
        .edit()
        .putString("data", Gson().toJson(value))
        .apply()

fun getWeathersDataPreference(c: Context): LinkedHashMap<String, WeatherData> =
    getPreferences(c).getString("data", null)?.let { jsonParse(it) } ?: LinkedHashMap()

fun updateWeatherDataPreference(
    context: Context,
    lat: Double,
    lon: Double,
    locationName: String? = null,
    newCurrentWeather: CurrentWeather? = null,
    newFutureWeather: List<FutureWeather>? = null
) {
    val weathersData = getWeathersDataPreference(context)
    val locationWeatherData = weathersData["$lat/$lon"]

    weathersData["$lat/$lon"] = WeatherData(
        lat = lat,
        lon = lon,
        name = locationName ?: locationWeatherData?.name,
        currentWeather =  newCurrentWeather ?: locationWeatherData?.currentWeather,
        futureWeather =  newFutureWeather ?: locationWeatherData?.futureWeather,
    )

    savePreference(context, weathersData)
}

fun getFutureWeatherPreference(c: Context, lat: Double, lon: Double) =
    getWeathersDataPreference(c)["$lat/$lon"]?.futureWeather ?: emptyList()

fun removeWeathersDataPreference(c: Context, weatherData: WeatherData) {
    val weathersData = getWeathersDataPreference(c)

    weathersData.remove("${weatherData.lat}/${weatherData.lon}")

    savePreference(c, weathersData)
}
