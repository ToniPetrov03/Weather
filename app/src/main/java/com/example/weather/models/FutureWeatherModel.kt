package com.example.weather.models

data class FutureWeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>
)

data class WeatherItem(
    val dt: Int,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?,
    val sys: Sys,
    val dt_txt: String
)

data class FutureWeather(
    val id: Int,
    val date: String?,
    val hour: String,
    val icon: String,
    val description: String,
    val temperature: String,
    val feelsLike: String,
    val windSpeed: Double,
    val chanceOfRain: String,
)
