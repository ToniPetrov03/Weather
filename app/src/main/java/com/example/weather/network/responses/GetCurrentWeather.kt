package com.example.weather.network.responses

import com.example.weather.BuildConfig
import com.example.weather.LANG
import com.example.weather.UNIT
import com.example.weather.WEATHER_BASE_URL
import com.example.weather.network.models.CurrentWeather
import com.example.weather.network.models.CurrentWeatherResponse
import com.example.weather.utils.capitalize
import com.example.weather.utils.formatTime
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import kotlin.math.roundToInt

suspend fun getCurrentWeather(lat: Double, lon: Double): CurrentWeather {
    return withContext(Dispatchers.IO) {
        val response = withContext(Dispatchers.IO) {
            URL("$WEATHER_BASE_URL/weather?lat=$lat&lon=$lon&units=$UNIT&lang=$LANG&appid=${BuildConfig.WEATHER_API_KEY}")
                .readText()
        }

        mapCurrentWeatherResponse(Gson().fromJson(response, CurrentWeatherResponse::class.java))
    }
}

private fun mapCurrentWeatherResponse(currentWeather: CurrentWeatherResponse): CurrentWeather {
    return CurrentWeather(
        lat = currentWeather.coord.lat,
        lon = currentWeather.coord.lon,
        icon = currentWeather.weather[0].icon,
        windSpeed = currentWeather.wind.speed,
        sunset = formatTime(currentWeather.sys.sunset),
        sunrise = formatTime(currentWeather.sys.sunrise),
        description = capitalize(currentWeather.weather[0].description),
        temperature = currentWeather.main.temp.roundToInt().toString(),
        feelsLike = currentWeather.main.feels_like.roundToInt().toString(),
    )
}
