package com.example.weather.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.util.GeoPoint

class LocationHelper(
    private val context: Context,
    private val permissionLauncher: ActivityResultLauncher<String>,
    private val settingsLauncher: ActivityResultLauncher<IntentSenderRequest>,
    private val onLocationReceived: (GeoPoint) -> Unit
) {

    fun getLocation() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_BALANCED_POWER_ACCURACY,
            0
        ).build()

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            val hasFine = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            val hasCoarse = ContextCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasFine && !hasCoarse) {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return@addOnSuccessListener
            }

            LocationServices.getFusedLocationProviderClient(context)
                .getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    CancellationTokenSource().token
                ).addOnSuccessListener { location ->
                    location?.let {
                        onLocationReceived(GeoPoint(it.latitude, it.longitude))
                    }
                }
        }

        task.addOnFailureListener {
            if (it is ResolvableApiException) {
                settingsLauncher.launch(IntentSenderRequest.Builder(it.resolution).build())
            }
        }
    }
}

@Composable
fun rememberLocationHelper(
    onLocationReceived: (GeoPoint) -> Unit
): LocationHelper {
    val context = LocalContext.current
    val locationHelperRef = remember { mutableStateOf<LocationHelper?>(null) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            locationHelperRef.value?.getLocation()
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        if (it.resultCode == android.app.Activity.RESULT_OK) {
            locationHelperRef.value?.getLocation()
        }
    }

    val helper = remember {
        LocationHelper(
            context,
            permissionLauncher,
            settingsLauncher,
            onLocationReceived
        )
    }

    locationHelperRef.value = helper

    return helper
}
