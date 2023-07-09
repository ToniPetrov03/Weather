package com.example.weather.network

import com.example.weather.BuildConfig
import com.example.weather.LANG
import com.example.weather.UNIT
import com.example.weather.WEATHER_BASE_URL
import com.example.weather.models.FutureWeather
import com.example.weather.models.FutureWeatherResponse
import com.example.weather.utils.capitalize
import com.example.weather.utils.formatDate
import com.example.weather.utils.formatTime
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.roundToInt

suspend fun getFutureWeather(lat: Double, lon: Double): List<FutureWeather> {
    return withContext(Dispatchers.IO) {
        val response = withContext(Dispatchers.IO) {
            URL("$WEATHER_BASE_URL/forecast?lat=$lat&lon=$lon&units=$UNIT&lang=$LANG&appid=${BuildConfig.WEATHER_API_KEY}")
                .readText()
        }

        mapFutureWeatherResponse(Gson().fromJson(response, FutureWeatherResponse::class.java))
    }
}

private fun mapFutureWeatherResponse(futureWeather: FutureWeatherResponse): List<FutureWeather> {
    var previousDate = ""

    return futureWeather.list.map { item ->
        val formattedDate = capitalize(formatDate(item.dt))
        val date = formattedDate.takeUnless { it == previousDate }
        previousDate = formattedDate

        FutureWeather(
            date,
            hour = formatTime(item.dt),
            icon = item.weather[0].icon,
            windSpeed = item.wind.speed,
            chanceOfRain = (item.pop * 100).toInt().toString(),
            description = capitalize(item.weather[0].description),
            temperature = item.main.temp.roundToInt().toString(),
            feelsLike = item.main.feels_like.roundToInt().toString(),
        )
    }
}
