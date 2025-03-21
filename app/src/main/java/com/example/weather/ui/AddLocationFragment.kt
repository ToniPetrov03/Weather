package com.example.weather.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentAddLocationBinding
import com.example.weather.models.Location
import com.example.weather.utils.LocationCallback
import com.example.weather.utils.addLocationPreference
import com.example.weather.utils.getCurrentLocation
import com.example.weather.utils.requestLocationPermission
import com.example.weather.utils.requestTurnOnGPS
import com.example.weather.utils.toggleVisibility
import org.osmdroid.config.Configuration
import org.osmdroid.library.BuildConfig
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class AddLocationFragment : Fragment(R.layout.fragment_add_location) {

    private var _binding: FragmentAddLocationBinding? = null
    private val binding get() = _binding!!

    private var selectedLocation = GeoPoint(42.7, 23.34)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().title = getString(R.string.adding_location)

        _binding = FragmentAddLocationBinding.bind(view)

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        setupViews()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() = with(binding) {
        map.setMultiTouchControls(true)
        map.controller.setCenter(selectedLocation)
        map.controller.setZoom(13.0)

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
            getCurrentLocation(requireContext(), object : LocationCallback {
                override fun onLocationReceived(location: GeoPoint) = with(binding) {
                    toggleVisibility(spinner, currentLocationButton)
                    selectedLocation = location
                    map.controller.animateTo(location)
                    updateMarker(map)
                }

                override fun onNoLocationPermission() {
                    toggleVisibility(spinner, currentLocationButton)
                    requestLocationPermission(requireActivity())
                }

                override fun onNoLocationProvided() {
                    toggleVisibility(spinner, currentLocationButton)
                    AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.gps_not_activated_title))
                        .setMessage(getString(R.string.gps_not_activated_description))
                        .setPositiveButton(getString(R.string.activate)) { _, _ ->
                            requestTurnOnGPS(requireActivity())
                        }
                        .setNegativeButton(getString(R.string.cancel_action), null)
                        .show()
                }
            })
        }

        locationName.doAfterTextChanged { addButton.isEnabled = !it.isNullOrEmpty() }

        addButton.setOnClickListener {
            addLocationPreference(
                context = requireContext(),
                value = Location(
                    name = locationName.text.toString(),
                    lat = selectedLocation.latitude,
                    lon = selectedLocation.longitude,
                )
            )

            findNavController().navigateUp()
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
