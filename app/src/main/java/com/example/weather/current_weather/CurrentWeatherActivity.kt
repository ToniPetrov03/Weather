package com.example.weather.current_weather

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.add_location.AddLocationActivity
import com.example.weather.utils.getWeathersDataPreference
import com.example.weather.utils.updateWeatherDataPreference
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

        binding.weatherSwipeRefresh.isRefreshing = true
        binding.weatherSwipeRefresh.setOnRefreshListener { getCurrentWeather() }

        getCurrentWeather()
    }

    private fun getCurrentWeather() {
        val weatherPreference = getWeathersDataPreference(this).values.toList()
        weatherAdapter.updateWeather(weatherPreference)

        lifecycleScope.launch(Dispatchers.IO) {
            val newWeather = weatherPreference.map {
                getCurrentWeather(it.lat, it.lon)?.let { weather ->
                    updateWeatherDataPreference(
                        context = this@CurrentWeatherActivity,
                        lat = it.lat,
                        lon = it.lon,
                        newCurrentWeather = weather
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
