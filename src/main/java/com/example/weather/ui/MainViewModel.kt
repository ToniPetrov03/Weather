package com.example.weather.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weather.api.CurrentWeather
import com.example.weather.api.FutureWeather
import com.example.weather.api.WeatherAPI
import com.example.weather.models.ResponseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class MainViewModel : ViewModel() {

    private val _currentWeatherState =
        MutableStateFlow<ResponseState<List<CurrentWeather>>>(ResponseState.Loading)
    val currentWeatherState = _currentWeatherState.asStateFlow()

    private val cachedFutureWeathers = mutableMapOf<GeoPoint, List<FutureWeather>>()
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

    fun getCurrentWeather(locations: List<GeoPoint>, forceRefresh: Boolean = false) {
        _currentWeatherState.value.let {
            if (!forceRefresh && it is ResponseState.Success && it.body.size == locations.size) {
                return
            }
        }

        viewModelScope.launch {
            _currentWeatherState.emit(ResponseState.Loading)

            val response = WeatherAPI.getCurrentWeather(locations)

            if (locations.isNotEmpty() && response.isEmpty()) {
                _currentWeatherState.emit(ResponseState.Error)
            } else {
                _currentWeatherState.emit(ResponseState.Success(response))
            }
        }
    }

    fun getFutureWeather(geoPoint: GeoPoint, forceRefresh: Boolean = false) {
        cachedFutureWeathers[geoPoint]?.let {
            if (!forceRefresh) {
                _futureWeatherState.value = ResponseState.Success(it)
                return
            }
        }

        viewModelScope.launch {
            _futureWeatherState.emit(ResponseState.Loading)

            val response = WeatherAPI.getFutureWeather(geoPoint)

            if (response.isEmpty()) {
                _futureWeatherState.emit(ResponseState.Error)
            } else {
                cachedFutureWeathers[geoPoint] = response
                _futureWeatherState.emit(ResponseState.Success(response))
            }
        }
    }
}
