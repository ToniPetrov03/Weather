package com.example.weather.api

import com.example.weather.BuildConfig
import com.example.weather.utils.DateUtils.formatDate
import com.example.weather.utils.DateUtils.formatTime
import com.example.weather.utils.Utils.capitalize
import com.example.weather.utils.Utils.jsonParse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import org.osmdroid.util.GeoPoint
import java.io.IOException
import java.net.URL
import kotlin.math.roundToInt

const val LANG = "bg"

internal object WeatherAPI {

    private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5"
    private const val UNIT = "metric"
    private const val KEY = BuildConfig.WEATHER_API_KEY

    suspend fun getCurrentWeather(locations: List<GeoPoint>) = supervisorScope {
        locations.map {
            async(Dispatchers.IO) {
                try {
                    val response = jsonParse<CurrentWeatherResponse>(
                        URL("$WEATHER_BASE_URL/weather?lat=${it.latitude}&lon=${it.longitude}&units=$UNIT&lang=$LANG&appid=$KEY").readText()
                    )

                    CurrentWeather(
                        geoPoint = it,
                        id = response.id,
                        name = response.name,
                        icon = response.weather[0].icon,
                        windSpeed = response.wind.speed,
                        cloudiness = response.clouds.all,
                        sunset = formatTime(response.sys.sunset),
                        sunrise = formatTime(response.sys.sunrise),
                        temperature = response.main.temp.roundToInt(),
                        feelsLike = response.main.feels_like.roundToInt(),
                        description = capitalize(response.weather[0].description),
                    )
                } catch (_: IOException) {
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

    suspend fun getFutureWeather(geoPoint: GeoPoint) = withContext(Dispatchers.IO) {
        try {
            val response = jsonParse<FutureWeatherResponse>(
                URL("$WEATHER_BASE_URL/forecast?lat=${geoPoint.latitude}&lon=${geoPoint.longitude}&units=$UNIT&lang=$LANG&appid=$KEY").readText()
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
                    cloudiness = it.clouds.all,
                    temperature = it.main.temp.roundToInt(),
                    chanceOfRain = (it.pop * 100).roundToInt(),
                    feelsLike = it.main.feels_like.roundToInt(),
                    description = capitalize(it.weather[0].description),
                )
            }
        } catch (_: IOException) {
            listOf()
        }
    }

    fun getIconURL(icon: String) = "https://openweathermap.org/img/wn/$icon@2x.png"
}
