package com.example.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.api.WeatherAPI
import com.example.weather.models.CurrentWeather
import com.example.weather.models.FutureWeather
import com.example.weather.models.Location
import com.example.weather.models.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class MainViewModel : ViewModel() {

    lateinit var weather: CurrentWeather

    private val weatherAPI = WeatherAPI()

    private val _currentWeatherState =
        MutableStateFlow<ResponseState<List<CurrentWeather>>>(ResponseState.Loading)
    val currentWeatherState = _currentWeatherState.asStateFlow()

    private val _futureWeatherState =
        MutableStateFlow<ResponseState<List<FutureWeather>>>(ResponseState.Loading)
    val futureWeatherState = _futureWeatherState.asStateFlow()

    fun removeCurrentWeatherItem(item: CurrentWeather) {
        _currentWeatherState.value.let {
            if (it is ResponseState.Success) {
                _currentWeatherState.value = ResponseState.Success(it.body - item)
            }
        }
    }

    fun getCurrentWeather(locations: List<Location>) {
        viewModelScope.launch(Dispatchers.IO) {
            _currentWeatherState.emit(ResponseState.Loading)

            val response = weatherAPI.getCurrentWeather(locations)

            if (locations.isNotEmpty() && response.isEmpty()) {
                _currentWeatherState.emit(ResponseState.Error)
            } else {
                _currentWeatherState.emit(ResponseState.Success(response))
            }
        }
    }

    fun getFutureWeather() {
        viewModelScope.launch(Dispatchers.IO) {
            _futureWeatherState.emit(ResponseState.Loading)

            val response = weatherAPI.getFutureWeather(weather.lat, weather.lon)

            if (response.isEmpty()) {
                _futureWeatherState.emit(ResponseState.Error)
            } else {
                _futureWeatherState.emit(ResponseState.Success(response))
            }
        }
    }
}
