package com.example.weather.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R
import com.example.weather.api.FutureWeather
import com.example.weather.models.ResponseState
import com.example.weather.utils.Utils.mapWindSpeedToText
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun FutureWeatherScreen(
    geoPoint: GeoPoint,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.futureWeatherState.collectAsState()
    val isLoading = state is ResponseState.Loading
    val refreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.getFutureWeather(geoPoint, true) }
    )

    LaunchedEffect(Unit) {
        viewModel.getFutureWeather(geoPoint)
    }

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(context.getString(R.string.forecast_title))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = context.getString(R.string.back)
                        )
                    }
                },
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullRefresh(refreshState)
                .padding(padding)
        ) {
            LazyColumn(Modifier.fillMaxSize()) {
                when (state) {
                    is ResponseState.Success -> {
                        items((state as ResponseState.Success<List<FutureWeather>>).body) {
                            FutureWeatherCard(it)
                        }
                    }

                    is ResponseState.Error -> {
                        item {
                            Text(
                                fontSize = 18.sp,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                text = context.getString(R.string.error_message)
                            )
                        }
                    }

                    else -> {}
                }
            }

            PullRefreshIndicator(
                refreshing = isLoading,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun FutureWeatherCard(weather: FutureWeather) {
    val context = LocalContext.current

    Column {
        if (!weather.date.isNullOrEmpty()) {
            Text(
                text = weather.date,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )
        }

        WeatherCard(
            icon = weather.icon,
            name = weather.hour,
            description = weather.description,
            temperature = weather.temperature,
            feelsLike = weather.feelsLike,
            info1 = mapWindSpeedToText(context, weather.windSpeed),
            info2 = context.getString(R.string.cloudiness, weather.cloudiness),
            info3 = context.getString(R.string.chance_of_rain, weather.chanceOfRain),
        )
    }
}
