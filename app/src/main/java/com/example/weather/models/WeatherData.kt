package com.example.weather.models

import com.example.weather.current_weather.CurrentWeather
import com.example.weather.future_weather.FutureWeather

data class WeatherData(
    var lat: Double,
    var lon: Double,
    var name: String?,
    var currentWeather: CurrentWeather?,
    var futureWeather: List<FutureWeather>?
)
