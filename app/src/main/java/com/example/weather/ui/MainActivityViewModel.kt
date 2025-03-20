package com.example.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.api.WeatherAPI
import com.example.weather.models.CurrentWeather
import com.example.weather.models.FutureWeather
import com.example.weather.models.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MainActivityViewModel : ViewModel() {

    lateinit var weather: CurrentWeather

    private val weatherAPI = WeatherAPI()

    private val _currentWeatherState = MutableStateFlow<List<CurrentWeather>>(listOf())
    val currentWeatherState = _currentWeatherState.asStateFlow()

    private val _futureWeatherState = MutableStateFlow<List<FutureWeather>>(listOf())
    val futureWeatherState = _futureWeatherState.asStateFlow()

    fun getCurrentWeather(locations: List<Location>) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentWeatherState.emit(weatherAPI.getCurrentWeather(locations))
        }
    }

    fun getFutureWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            val response = weatherAPI.getFutureWeather(weather.lat, weather.lon)

            if (response is List<FutureWeather>) {
                _futureWeatherState.emit(response)
            }
        }
    }
}
