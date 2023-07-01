package com.example.weather.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.weather.models.WeatherAppScreens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherApp(viewModel: MainViewModel) {
    var currentScreen by remember {
        mutableStateOf<WeatherAppScreens>(WeatherAppScreens.CurrentWeatherScreen)
    }

    when (val screen = currentScreen) {
        is WeatherAppScreens.CurrentWeatherScreen -> {
            CurrentWeatherScreen(
                viewModel = viewModel,
                onAddLocation = { currentScreen = WeatherAppScreens.AddLocationScreen },
                onOpenDetails = { currentScreen = WeatherAppScreens.FutureWeatherScreen(it) }
            )
        }

        is WeatherAppScreens.FutureWeatherScreen -> {
            FutureWeatherScreen(
                viewModel = viewModel,
                geoPoint = screen.geoPoint,
                onBack = { currentScreen = WeatherAppScreens.CurrentWeatherScreen }
            )
        }

        is WeatherAppScreens.AddLocationScreen -> {
            AddLocationScreen(
                onBack = { currentScreen = WeatherAppScreens.CurrentWeatherScreen }
            )
        }
    }
}
