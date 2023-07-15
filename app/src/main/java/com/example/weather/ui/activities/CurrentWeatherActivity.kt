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
import com.example.weather.network.getCurrentWeather
import com.example.weather.ui.adapters.CurrentWeatherAdapter
import com.example.weather.utils.getWeathersDataPreference
import com.example.weather.utils.updateWeatherPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        return when (item.itemId) {
            R.id.add_location -> {
                addLocationActivityResultLauncher.launch(AddLocationActivity.getIntent(this))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
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
        val weatherPreference = getWeathersDataPreference(this).values
        weatherAdapter.updateWeather(weatherPreference.toList())

        lifecycleScope.launch(Dispatchers.IO) {
            val newWeather = weatherPreference.map {
                getCurrentWeather(it.lat, it.lon)?.let { weather ->
                    updateWeatherPreference(
                        this@CurrentWeatherActivity,
                        it.lat,
                        it.lon,
                        weather
                    )

                    it.copy(currentWeather = weather)
                } ?: it
            }

            launch(Dispatchers.Main) {
                weatherAdapter.updateWeather(newWeather)
                binding.weatherSwipeRefresh.isRefreshing = false
            }
        }
    }
}
