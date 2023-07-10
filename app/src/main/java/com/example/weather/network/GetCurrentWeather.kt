package com.example.weather.network

import android.util.Log
import com.example.weather.BuildConfig
import com.example.weather.LANG
import com.example.weather.UNIT
import com.example.weather.WEATHER_BASE_URL
import com.example.weather.models.CurrentWeather
import com.example.weather.models.CurrentWeatherResponse
import com.example.weather.utils.capitalize
import com.example.weather.utils.formatTime
import com.example.weather.utils.jsonParse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import kotlin.math.roundToInt

suspend fun getCurrentWeather(lat: Double, lon: Double) = withContext(Dispatchers.IO) {
    try {
        val response: CurrentWeatherResponse = jsonParse(
            URL("$WEATHER_BASE_URL/weather?lat=$lat&lon=$lon&units=$UNIT&lang=$LANG&appid=${BuildConfig.WEATHER_API_KEY}")
                .readText()
        )

        CurrentWeather(
            dt = response.dt,
            lat = response.coord.lat,
            lon = response.coord.lon,
            icon = response.weather[0].icon,
            windSpeed = response.wind.speed,
            sunset = formatTime(response.sys.sunset),
            sunrise = formatTime(response.sys.sunrise),
            description = capitalize(response.weather[0].description),
            temperature = response.main.temp.roundToInt().toString(),
            feelsLike = response.main.feels_like.roundToInt().toString(),
        )
    } catch (e: IOException) {
        Log.e("getCurrentWeather", e.toString())
        null
    }
}
