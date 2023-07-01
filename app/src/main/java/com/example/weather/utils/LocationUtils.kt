package com.example.weather.utils

import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.util.GeoPoint

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


