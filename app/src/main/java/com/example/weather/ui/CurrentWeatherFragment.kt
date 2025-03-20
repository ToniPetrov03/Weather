package com.example.weather.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.models.Location
import com.example.weather.utils.getLocationsPreference
import com.example.weather.utils.removeLocationPreference
import kotlinx.coroutines.launch

internal class CurrentWeatherFragment : Fragment(R.layout.fragment_weather) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_add_location, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_location -> {
                findNavController().navigate(R.id.action_add_location)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        val binding = FragmentWeatherBinding.bind(view)
        val adapter = CurrentWeatherAdapter(
            onClickListener = {
                viewModel.weather = it
                findNavController().navigate(R.id.action_future_weather)
            },
            onRemoveListener = {
                (binding.weatherRv.adapter as CurrentWeatherAdapter).removeItem(it)
                removeLocationPreference(
                    context = requireContext(),
                    value = Location(
                        name = it.name,
                        lat = it.lat,
                        lon = it.lon,
                    )
                )
            }
        )
        binding.weatherRv.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentWeatherState.collect {
                    adapter.submitList(it)
                }
            }
        }
        viewModel.getCurrentWeather(getLocationsPreference(requireContext()))
    }
}
