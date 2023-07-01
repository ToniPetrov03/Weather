package com.example.weather.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.weather.R
import com.example.weather.utils.PreferenceUtils.addLocationPreference
import com.example.weather.utils.rememberLocationHelper
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("ClickableViewAccessibility")
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun AddLocationScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var isLocationLoading by remember { mutableStateOf(false) }
    var selectedLocation by remember { mutableStateOf(GeoPoint(42.6557, 23.3626)) }

    fun MapView.updateMarker() {
        this.overlays.clear()
        this.overlays.add(Marker(this).apply { position = selectedLocation })
        this.invalidate()
    }

    val mapView = remember {
        MapView(context).apply {
            setMultiTouchControls(true)
            controller.setCenter(selectedLocation)
            controller.setZoom(13.0)
            this.updateMarker()
        }
    }

    val locationHelper = rememberLocationHelper {
        selectedLocation = it
        mapView.updateMarker()
        mapView.controller.animateTo(selectedLocation)
        isLocationLoading = false
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(context.getString(R.string.new_location))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = context.getString(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            addLocationPreference(context, selectedLocation)
                            onBack()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = context.getString(R.string.add_location)
                        )
                    }

                    IconButton(
                        onClick = {
                            isLocationLoading = true
                            locationHelper.getLocation()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = context.getString(R.string.current_location),
                            tint = if (isLocationLoading) {
                                Color(context.getColor(R.color.sky_blue))
                            } else LocalContentColor.current
                        )
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            factory = { mapView },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) { map ->
            map.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    val point = map.projection.fromPixels(event.x.toInt(), event.y.toInt())
                    selectedLocation = GeoPoint(point.latitude, point.longitude)
                    map.updateMarker()
                }
                false
            }
        }
    }
}
