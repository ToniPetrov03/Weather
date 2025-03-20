package com.example.weather.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weather.R
import com.example.weather.databinding.FragmentWeatherBinding
import kotlinx.coroutines.launch

internal class FutureWeatherFragment : Fragment(R.layout.fragment_weather) {

    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().title = getString(R.string.forecast_title)

        val binding = FragmentWeatherBinding.bind(view)
        val adapter = FutureWeatherAdapter()

        binding.weatherRv.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.futureWeatherState.collect {
                    adapter.submitList(it)
                }
            }
        }
        viewModel.getFutureWeather()
    }
}
