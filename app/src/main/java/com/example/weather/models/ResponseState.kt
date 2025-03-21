package com.example.weather.models

sealed interface ResponseState<out T> {
    data object Loading : ResponseState<Nothing>
    data object Error : ResponseState<Nothing>
    data class Success<T>(val body: T) : ResponseState<T>
}
