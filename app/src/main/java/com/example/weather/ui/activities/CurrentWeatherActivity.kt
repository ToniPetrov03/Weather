package com.example.weather.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.network.models.CurrentWeather
import com.example.weather.network.responses.getCurrentWeather
import com.example.weather.ui.adapters.CurrentWeatherAdapter
import com.example.weather.utils.hasInternetConnectivity
import com.example.weather.utils.readLocationPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

internal class CurrentWeatherActivity : AppCompatActivity() {
    private val weatherAdapter = CurrentWeatherAdapter(this)

    private val addLocationActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                initCurrentWeather()
            }
        }

    private lateinit var binding: WeatherActivityBinding

    private var lastLat: Double? = null
    private var lastLon: Double? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_location, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_location -> {
                addLocationActivityResultLauncher.launch(
                    AddLocationActivity.getIntent(
                        this,
                        lastLat,
                        lastLon
                    )
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WeatherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.weatherCards.apply {
            layoutManager = LinearLayoutManager(this@CurrentWeatherActivity)
            adapter = weatherAdapter
        }

        binding.weatherSwipeRefresh.setOnRefreshListener { initCurrentWeather() }

        initCurrentWeather()
    }

    private fun initCurrentWeather() = with(binding) {
        if (hasInternetConnectivity(this@CurrentWeatherActivity)) {
            connectionStatus.text = getString(R.string.online)
            connectionStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_circle,
                0,
                0,
                0
            )
            readLocationPreferences(this@CurrentWeatherActivity).let {
                lastLat = it.lastOrNull()?.lat
                lastLon = it.lastOrNull()?.lon
                it.takeIf {
                    lifecycleScope.launch {
                        val weatherQueue = ConcurrentLinkedQueue<CurrentWeather>()

                        for (location in it) {
                            weatherQueue.add(getCurrentWeather(location.lat, location.lon))
                        }

                        weatherAdapter.updateWeather(it, weatherQueue.toList())
                        binding.weatherSwipeRefresh.isRefreshing = false
                    }
                    it.isNotEmpty()
                }
            }
        } else {
            binding.connectionStatus.text = getString(R.string.offline)
            binding.connectionStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(
                R.drawable.ic_offline_pin,
                0,
                0,
                0
            )
            binding.weatherSwipeRefresh.isRefreshing = false
        }
    }
}
