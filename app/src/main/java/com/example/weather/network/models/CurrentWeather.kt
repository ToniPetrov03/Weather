package com.example.weather.network.models

data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val rain: Rain,
    val clouds: Clouds,
    val dt: Int,
    val sys: Sys,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int
)

data class CurrentWeather(
    val icon: String,
    val description: String,
    val temperature:String,
    val feelsLike: String,
    val windSpeed: Double,
    val sunrise: String,
    val sunset: String,
    val lat: Double,
    val lon: Double,
)
