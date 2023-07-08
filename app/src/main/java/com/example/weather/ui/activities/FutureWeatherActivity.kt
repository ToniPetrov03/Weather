package com.example.weather.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.databinding.WeatherActivityBinding
import com.example.weather.network.responses.getFutureWeather
import com.example.weather.ui.adapters.FutureWeatherAdapter
import com.example.weather.utils.getWeathersDataPreference
import com.example.weather.utils.hasInternetConnectivity
import com.example.weather.utils.updateWeatherPreference
import kotlinx.coroutines.launch

internal class FutureWeatherActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_NAME = "location_name"
        private const val LAT = "lat"
        private const val LON = "lon"

        fun getIntent(context: Context, locationName: String, lat: Double, lon: Double) =
            Intent(context, FutureWeatherActivity::class.java).apply {
                putExtra(LOCATION_NAME, locationName)
                putExtra(LAT, lat)
                putExtra(LON, lon)
            }
    }

    private lateinit var binding: WeatherActivityBinding

    private var futureWeatherAdapter = FutureWeatherAdapter(mutableListOf(), this)
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

        title = intent.getStringExtra(LOCATION_NAME)

        lat = intent.getDoubleExtra(LAT, 0.0)
        lon = intent.getDoubleExtra(LON, 0.0)

        binding.weatherCards.layoutManager = LinearLayoutManager(this)
        binding.weatherCards.adapter = futureWeatherAdapter

        binding.weatherSwipeRefresh.setOnRefreshListener { getFutureWeather() }

        getFutureWeather()
    }

    private fun getFutureWeather() {
        if (hasInternetConnectivity(this)) {
            lifecycleScope.launch {
                getFutureWeather(lat, lon).let {
                    futureWeatherAdapter.updateData(it)
                    updateWeatherPreference(
                        this@FutureWeatherActivity,
                        lat,
                        lon,
                        it,
                    )
                    binding.weatherSwipeRefresh.isRefreshing = false
                }

            }
        } else {
            getWeathersDataPreference(this)["$lat/$lon"]?.futureWeather?.let {
                futureWeatherAdapter.updateData(it)
            }
            binding.weatherSwipeRefresh.isRefreshing = false
        }
    }
}
