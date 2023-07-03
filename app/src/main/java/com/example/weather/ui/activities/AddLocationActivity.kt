package com.example.weather.ui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import com.example.weather.DEFAULT_LAT_LOCATION_BG
import com.example.weather.DEFAULT_LON_LOCATION_BG
import com.example.weather.DEFAULT_ZOOM
import com.example.weather.R
import com.example.weather.databinding.AddLocationActivityBinding
import com.example.weather.utils.Location
import com.example.weather.utils.LocationCallback
import com.example.weather.utils.addLocationPreferences
import com.example.weather.utils.getCurrentLocation
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
        private const val LAST_LAT = "lastLat"
        private const val LAST_LON = "lastLon"

        fun getIntent(context: Context, lastLat: Double?, lastLon: Double?) =
            Intent(context, AddLocationActivity::class.java).apply {
                putExtra(LAST_LAT, lastLat)
                putExtra(LAST_LON, lastLon)
            }
    }

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

        val lat = intent.getDoubleExtra(LAST_LAT, DEFAULT_LAT_LOCATION_BG)
        val lon = intent.getDoubleExtra(LAST_LON, DEFAULT_LON_LOCATION_BG)

        selectedLocation = GeoPoint(lat, lon)

        setupViews()
    }

    override fun onLocationReceived(location: GeoPoint) = with(binding) {
        toggleVisibility(spinner, currentLocationButton)
        selectedLocation = location
        map.controller.animateTo(location)
        updateMarker(map)
    }

    override fun onNoLocationPermission() = with(binding) {
        toggleVisibility(spinner, currentLocationButton)
        requestLocationPermission(this@AddLocationActivity)
    }

    override fun onNoLocationProvided(): Unit = with(binding) {
        toggleVisibility(spinner, currentLocationButton)
        AlertDialog.Builder(this@AddLocationActivity)
            .setTitle(getString(R.string.gps_not_activated_title))
            .setMessage(getString(R.string.gps_not_activated_description))
            .setPositiveButton(getString(R.string.activate)) { _, _ -> requestTurnOnGPS(this@AddLocationActivity) }
            .setNegativeButton(getString(R.string.cancel_action), null)
            .show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() = with(binding) {
        map.setMultiTouchControls(true)
        map.controller.setCenter(selectedLocation)
        map.controller.setZoom(DEFAULT_ZOOM)

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
            addLocationPreferences(
                this@AddLocationActivity,
                Location(
                    locationName.text.toString(),
                    selectedLocation.latitude,
                    selectedLocation.longitude
                )

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
