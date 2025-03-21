package com.example.weather.api

import android.util.Log
import com.example.weather.BuildConfig
import com.example.weather.models.CurrentWeather
import com.example.weather.models.CurrentWeatherResponse
import com.example.weather.models.FutureWeather
import com.example.weather.models.FutureWeatherResponse
import com.example.weather.models.Location
import com.example.weather.utils.capitalize
import com.example.weather.utils.formatDate
import com.example.weather.utils.formatTime
import com.example.weather.utils.jsonParse
import java.io.IOException
import java.net.URL
import kotlin.math.roundToInt

const val LANG = "bg"

internal class WeatherAPI {

    private val baseURL = "https://api.openweathermap.org/data/2.5"
    private val unit = "metric"
    private val key = BuildConfig.WEATHER_API_KEY

    private val iconsBaseURL = "https://openweathermap.org/img/wn/"
    private val iconsPNGFormat = "@2x.png"

    fun getCurrentWeather(locations: List<Location>) = locations.mapNotNull { (name, lat, lon) ->
        try {
            val response = jsonParse<CurrentWeatherResponse>(
                URL("$baseURL/weather?lat=${lat}&lon=${lon}&units=$unit&lang=$LANG&appid=$key").readText()
            )

            CurrentWeather(
                name = name,
                lat = lat,
                lon = lon,
                id = response.id,
                dt = response.dt,
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

    fun getFutureWeather(lat: Double, lon: Double) = try {
        val response = jsonParse<FutureWeatherResponse>(
            URL("$baseURL/forecast?lat=$lat&lon=$lon&units=$unit&lang=$LANG&appid=$key").readText()
        )

        var previousDate = ""

        response.list.map {
            val formattedDate = capitalize(formatDate(it.dt))
            val date = formattedDate.takeIf { previousDate != formattedDate }
            previousDate = formattedDate

            FutureWeather(
                id = it.dt,
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
        listOf()
    }

    fun getIconsURL(icon: String) = "$iconsBaseURL$icon$iconsPNGFormat"
}
