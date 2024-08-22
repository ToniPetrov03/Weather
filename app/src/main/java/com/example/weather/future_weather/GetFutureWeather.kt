package com.example.weather.future_weather

import android.util.Log
import com.example.weather.BuildConfig
import com.example.weather.utils.LANG
import com.example.weather.utils.UNIT
import com.example.weather.utils.WEATHER_BASE_URL
import com.example.weather.utils.capitalize
import com.example.weather.utils.formatDate
import com.example.weather.utils.formatTime
import com.example.weather.utils.jsonParse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL
import kotlin.math.roundToInt

suspend fun getFutureWeather(lat: Double, lon: Double) = withContext(Dispatchers.IO) {
    try {
        val response: FutureWeatherResponse = jsonParse(
            URL("$WEATHER_BASE_URL/forecast?lat=$lat&lon=$lon&units=$UNIT&lang=$LANG&appid=${BuildConfig.WEATHER_API_KEY}")
                .readText()
        )

        var previousDate = ""
        response.list.map {
            val formattedDate = capitalize(formatDate(it.dt))
            val date = formattedDate.takeIf { previousDate != formattedDate }
            previousDate = formattedDate

            FutureWeather(
                date = date,
                hour = formatTime(it.dt),
                icon = it.weather[0].icon,
                windSpeed = it.wind.speed,
                chanceOfRain = (it.pop * 100).toInt().toString(),
                description = capitalize(it.weather[0].description),
                temperature = it.main.temp.roundToInt().toString(),
                feelsLike = it.main.feels_like.roundToInt().toString(),
            )
        }
    } catch (e: IOException) {
        Log.e("getFutureWeather", e.toString())
        null
    }
}
