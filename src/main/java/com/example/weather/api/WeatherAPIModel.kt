package com.example.weather.api

import org.osmdroid.util.GeoPoint

/** ------------------- Common Models ------------------- **/

internal data class Coord(
    val lon: Double,
    val lat: Double,
)

internal data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String,
)

internal data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int,
    val grnd_level: Int,
)

internal data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double,
)

internal data class Clouds(
    val all: Int,
)

/** ----------- Current Weather Original Model ---------- **/

internal data class CurrentWeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val base: String,
    val main: Main,
    val visibility: Int,
    val wind: Wind,
    val clouds: Clouds,
    val rain: RainCurrentWeather,
    val snow: SnowCurrentWeather,
    val dt: Int,
    val sys: SysCurrentWeather,
    val timezone: Int,
    val id: Int,
    val name: String,
    val cod: Int,
)

internal data class RainCurrentWeather(
    val `1h`: Double,
)

internal data class SnowCurrentWeather(
    val `1h`: Double,
)

internal data class SysCurrentWeather(
    val type: Int,
    val id: Int,
    val country: String,
    val sunrise: Int,
    val sunset: Int,
)

/** ------------ Current Weather Mapped Model ----------- **/

data class CurrentWeather(
    val id: Int,
    val name: String,
    val icon: String,
    val sunset: String,
    val sunrise: String,
    val feelsLike: Int,
    val cloudiness: Int,
    val temperature: Int,
    val windSpeed: Double,
    val geoPoint: GeoPoint,
    val description: String,
)

/** ------------ Future Weather Original Model ---------- **/

internal data class FutureWeatherResponse(
    val cod: String,
    val message: Int,
    val cnt: Int,
    val list: List<WeatherItem>,
    val city: City,
)

internal data class WeatherItem(
    val dt: Int,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: RainFutureWeather,
    val snow: SnowFutureWeather,
    val sys: SysFutureWeather,
    val dt_txt: String,
)

internal data class RainFutureWeather(
    val `3h`: Double,
)

internal data class SnowFutureWeather(
    val `3h`: Double,
)

internal data class SysFutureWeather(
    val pod: String
)

internal data class City(
    val id: String,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Int,
    val sunset: Int,
)

/** ------------- Future Weather Mapped Model ----------- **/

data class FutureWeather(
    val id: Int,
    val date: String?,
    val hour: String,
    val icon: String,
    val feelsLike: Int,
    val cloudiness: Int,
    val temperature: Int,
    val windSpeed: Double,
    val chanceOfRain: Int,
    val description: String,
)
