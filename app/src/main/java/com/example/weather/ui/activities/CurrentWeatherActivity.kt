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
import com.example.weather.network.responses.getCurrentWeather
import com.example.weather.ui.adapters.CurrentWeatherAdapter
import com.example.weather.utils.WeatherData
import com.example.weather.utils.getWeathersDataPreference
import com.example.weather.utils.hasInternetConnectivity
import com.example.weather.utils.updateWeatherPreference
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

internal class CurrentWeatherActivity : AppCompatActivity() {

    private val addLocationActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                getCurrentWeather()
            }
        }

    private lateinit var binding: WeatherActivityBinding

    private val weatherAdapter = CurrentWeatherAdapter(this)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_add_location, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_location -> {
                addLocationActivityResultLauncher.launch(AddLocationActivity.getIntent(this))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WeatherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.weatherCards.layoutManager = LinearLayoutManager(this)
        binding.weatherCards.adapter = weatherAdapter

        binding.weatherSwipeRefresh.setOnRefreshListener { getCurrentWeather() }

        getCurrentWeather()
    }

    private fun getCurrentWeather() {
        val weathersData = getWeathersDataPreference(this).values

        if (weathersData.isEmpty()) {
            binding.weatherSwipeRefresh.isRefreshing = false
            return
        }

        if (hasInternetConnectivity(this)) {
            lifecycleScope.launch {
                val weatherQueue = ConcurrentLinkedQueue<WeatherData>()

                for (weatherData in weathersData) {
                    weatherQueue.add(
                        getCurrentWeather(weatherData.lat, weatherData.lon).let {
                            updateWeatherPreference(
                                this@CurrentWeatherActivity,
                                weatherData.lat,
                                weatherData.lon,
                                it,
                            )
                            weatherData.copy(currentWeather = it)
                        }
                    )
                }

                weatherAdapter.updateWeather(weatherQueue.toList())
                binding.weatherSwipeRefresh.isRefreshing = false
            }
        } else {
            weatherAdapter.updateWeather(weathersData.toList())
            binding.weatherSwipeRefresh.isRefreshing = false
        }
    }
}
