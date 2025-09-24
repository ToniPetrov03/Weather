package com.example.weather.ui

import android.app.AlertDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weather.R
import com.example.weather.api.CurrentWeather
import com.example.weather.models.ResponseState
import com.example.weather.utils.Utils.mapWindSpeedToText
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CurrentWeatherScreen(
    viewModel: MainViewModel,
    onAddLocation: () -> Unit,
    onOpenDetails: (GeoPoint) -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.currentWeatherState.collectAsState()
    val isLoading = state is ResponseState.Loading
    val refreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = { viewModel.getCurrentWeather(true) }
    )

    LaunchedEffect(Unit) {
        viewModel.getCurrentWeather()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(context.getString(R.string.weather))
                },
                actions = {
                    IconButton(onClick = onAddLocation) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = context.getString(R.string.add_location)
                        )
                    }
                }
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
                        items(
                            items = (state as ResponseState.Success<List<CurrentWeather>>).body,
                            key = { it.geoPoint }
                        ) {
                            CurrentWeatherCard(
                                weather = it,
                                onClick = { onOpenDetails(it.geoPoint) },
                                onRemove = { viewModel.removeCurrentWeatherItem(it) }
                            )
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CurrentWeatherCard(
    weather: CurrentWeather,
    onClick: () -> Unit,
    onRemove: () -> Unit,
) {
    val context = LocalContext.current

    val dismissState = rememberDismissState(
        confirmStateChange = { dismissValue ->
            if (dismissValue == DismissValue.DismissedToStart) {
                AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.removing_location_title))
                    .setMessage(
                        context.getString(
                            R.string.removing_location_description,
                            weather.name
                        )
                    )
                    .setPositiveButton(context.getString(R.string.continue_action)) { _, _ ->
                        onRemove()
                    }
                    .setNeutralButton(context.getString(R.string.cancel_action), null)
                    .show()
            }
            false
        }
    )

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            val color = when (dismissState.dismissDirection) {
                DismissDirection.EndToStart -> Color(context.getColor(R.color.coral))
                else -> Color.Transparent
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = context.getString(R.string.removing_location_title),
                    tint = Color.White
                )
            }
        }
    ) {
        WeatherCard(
            icon = weather.icon,
            name = weather.name,
            description = weather.description,
            temperature = weather.temperature,
            feelsLike = weather.feelsLike,
            info1 = mapWindSpeedToText(context, weather.windSpeed),
            info2 = context.getString(R.string.cloudiness, weather.cloudiness),
            info3 = context.getString(R.string.sunrise_sunset, weather.sunrise, weather.sunset),
            onClick = onClick,
        )
    }
}
