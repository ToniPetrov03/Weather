package com.example.weather.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.weather.R
import com.example.weather.databinding.FragmentWeatherBinding
import com.example.weather.models.ResponseState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class FutureWeatherFragment : Fragment(R.layout.fragment_weather), MenuProvider {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_main, menu)
        menu.findItem(R.id.reload).isVisible = true
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.reload -> {
            viewModel.getFutureWeather()
            true
        }

        else -> false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requireActivity().addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        requireActivity().title = getString(R.string.forecast_title)

        val binding = FragmentWeatherBinding.bind(view)
        val adapter = FutureWeatherAdapter()
        val alertDialog = AlertDialog.Builder(context)
            .setTitle(getString(R.string.error_title))
            .setMessage(
                getString(R.string.error_description)
            )
            .setPositiveButton(getString(R.string.ok_action), null)
            .create()

        binding.weatherRv.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.futureWeatherState.collectLatest {
                    when (it) {
                        is ResponseState.Success -> adapter.submitList(it.body)
                        is ResponseState.Error -> alertDialog.show()
                        is ResponseState.Loading -> {}
                    }
                }
            }
        }
        viewModel.getFutureWeather()
    }
}
