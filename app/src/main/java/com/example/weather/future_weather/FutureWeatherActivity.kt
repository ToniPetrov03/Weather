package com.example.weather.future_weather

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.R
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.utils.getFutureWeatherPreference
import com.example.weather.utils.updateWeatherDataPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

internal class FutureWeatherActivity : AppCompatActivity() {
    companion object {
        private const val LAT = "lat"
        private const val LON = "lon"

        fun getIntent(context: Context, lat: Double, lon: Double) =
            Intent(context, FutureWeatherActivity::class.java).apply {
                putExtra(LAT, lat)
                putExtra(LON, lon)
            }
    }

    private val futureWeatherAdapter = FutureWeatherAdapter(mutableListOf(), this)

    private lateinit var binding: WeatherActivityBinding

    private var lat: Double = 0.0
    private var lon: Double = 0.0

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = WeatherActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        title = getString(R.string.forecast_title)

        lat = intent.getDoubleExtra(LAT, 0.0)
        lon = intent.getDoubleExtra(LON, 0.0)

        binding.weatherCards.layoutManager = LinearLayoutManager(this)
        binding.weatherCards.adapter = futureWeatherAdapter

        binding.weatherSwipeRefresh.isRefreshing = true
        binding.weatherSwipeRefresh.setOnRefreshListener { getFutureWeather() }

        futureWeatherAdapter.updateWeather(
            getFutureWeatherPreference(this@FutureWeatherActivity, lat, lon)
        )

        getFutureWeather()
    }

    private fun getFutureWeather() {
        lifecycleScope.launch(Dispatchers.IO) {
            val futureWeather = getFutureWeather(lat, lon).also {
                binding.weatherSwipeRefresh.isRefreshing = false
            } ?: return@launch

            launch(Dispatchers.Main) {
                futureWeatherAdapter.updateWeather(futureWeather)
                updateWeatherDataPreference(
                    context = this@FutureWeatherActivity,
                    lat = lat,
                    lon = lon,
                    newFutureWeather = futureWeather,
                )
            }
        }
    }
}
