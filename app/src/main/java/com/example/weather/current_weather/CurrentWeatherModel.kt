package com.example.weather.current_weather

import com.example.weather.models.Clouds
import com.example.weather.models.Coord
import com.example.weather.models.Main
import com.example.weather.models.Rain
import com.example.weather.models.Sys
import com.example.weather.models.Weather
import com.example.weather.models.Wind

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
    val dt: Int,
    val lat: Double,
    val lon: Double,
    val icon: String,
    val sunset: String,
    val sunrise: String,
    val windSpeed: Double,
    val feelsLike: String,
    val temperature:String,
    val description: String,
)
