package com.example.weather.models

import org.osmdroid.util.GeoPoint

sealed class WeatherAppScreens {
    data object AddLocationScreen : WeatherAppScreens()
    data object CurrentWeatherScreen : WeatherAppScreens()
    data class FutureWeatherScreen(val geoPoint: GeoPoint) : WeatherAppScreens()
}
