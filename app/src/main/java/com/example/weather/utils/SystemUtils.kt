package com.example.weather.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.osmdroid.util.GeoPoint

private const val LOCATION_PERMISSION_REQUEST_CODE = 1
private const val LOCATION_SETTINGS_REQUEST_CODE = 2

fun checkHasLocationPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}

fun requestLocationPermission(activity: Activity) {
    if (ContextCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }
}

fun requestTurnOnGPS(activity: Activity) {
    activity.startActivityForResult(
        Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
        LOCATION_SETTINGS_REQUEST_CODE
    )
}

fun getCurrentLocation(context: Context, callback: LocationCallback) {
    val locationManager =
        context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager

    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            callback.onLocationReceived(GeoPoint(location.latitude, location.longitude))
            locationManager.removeUpdates(this)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    if (checkHasLocationPermission(context)) {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )
        } else {
            callback.onNoLocationProvided()
        }
    } else {
        callback.onNoLocationPermission()
    }
}

interface LocationCallback {
    fun onLocationReceived(location: GeoPoint)
    fun onNoLocationPermission()
    fun onNoLocationProvided()
}
