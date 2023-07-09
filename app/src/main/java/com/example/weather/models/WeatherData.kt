package com.example.weather.models

data class WeatherData(
    val name: String,
    val lat: Double,
    val lon: Double,
    var currentWeather: CurrentWeather?,
    var futureWeather: List<FutureWeather>?
)
