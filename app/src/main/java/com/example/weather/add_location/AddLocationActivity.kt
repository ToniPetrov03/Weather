package com.example.weather.add_location

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.weather.R
import com.example.weather.databinding.AddLocationActivityBinding
import com.example.weather.utils.LocationCallback
import com.example.weather.utils.updateWeatherDataPreference
import com.example.weather.utils.getCurrentLocation
import com.example.weather.utils.getWeathersDataPreference
import com.example.weather.utils.requestLocationPermission
import com.example.weather.utils.requestTurnOnGPS
import com.example.weather.utils.toggleVisibility
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class AddLocationActivity : AppCompatActivity(), LocationCallback {
    companion object {
        fun getIntent(context: Context) = Intent(context, AddLocationActivity::class.java)
    }

    private val defaultLan = 42.7
    private val defaultLon = 23.3
    private val defaultZoom = 15.0

    private lateinit var binding: AddLocationActivityBinding
    private lateinit var selectedLocation: GeoPoint

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddLocationActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        val lastLocation = getWeathersDataPreference(this).values.lastOrNull()

        selectedLocation = GeoPoint(
            lastLocation?.lat ?: defaultLan,
            lastLocation?.lon ?: defaultLon
        )

        setupViews()
    }

    override fun onLocationReceived(location: GeoPoint) = with(binding) {
        toggleVisibility(spinner, currentLocationButton)
        selectedLocation = location
        map.controller.animateTo(location)
        updateMarker(map)
    }

    override fun onNoLocationPermission() {
        toggleVisibility(binding.spinner, binding.currentLocationButton)
        requestLocationPermission(this)
    }

    override fun onNoLocationProvided() {
        toggleVisibility(binding.spinner, binding.currentLocationButton)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.gps_not_activated_title))
            .setMessage(getString(R.string.gps_not_activated_description))
            .setPositiveButton(getString(R.string.activate)) { _, _ -> requestTurnOnGPS(this) }
            .setNegativeButton(getString(R.string.cancel_action), null)
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() = with(binding) {
        map.setMultiTouchControls(true)
        map.controller.setCenter(selectedLocation)
        map.controller.setZoom(defaultZoom)

        updateMarker(map)

        map.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val point = map.projection.fromPixels(event.x.toInt(), event.y.toInt())
                selectedLocation = GeoPoint(point.latitude, point.longitude)
                updateMarker(map)
            }
            false
        }

        currentLocationButton.setOnClickListener {
            toggleVisibility(spinner, currentLocationButton)
            getCurrentLocation(this@AddLocationActivity, this@AddLocationActivity)
        }

        locationName.requestFocus()
        locationName.doAfterTextChanged { addButton.isEnabled = !it.isNullOrEmpty() }

        addButton.setOnClickListener {
            updateWeatherDataPreference(
                context = this@AddLocationActivity,
                lat = selectedLocation.latitude,
                lon = selectedLocation.longitude,
                locationName = locationName.text.toString(),
            )
            setResult(RESULT_OK)
            finish()
        }
    }

    private fun updateMarker(map: MapView) {
        val marker = Marker(map)
        marker.position = selectedLocation
        map.overlays.clear()
        map.overlays.add(marker)
        map.invalidate()
    }
}
