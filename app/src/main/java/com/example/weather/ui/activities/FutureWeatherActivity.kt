package com.example.weather.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.network.responses.getFutureWeather
import com.example.weather.ui.adapters.FutureWeatherAdapter
import kotlinx.coroutines.launch

internal class FutureWeatherActivity : AppCompatActivity() {
    companion object {
        private const val LAT = "lat"
        private const val LON = "lon"
        private const val LOCATION = "location"

        fun getIntent(context: Context, lat: Double, lon: Double, location: String) =
            Intent(context, FutureWeatherActivity::class.java).apply {
                putExtra(LAT, lat)
                putExtra(LON, lon)
                putExtra(LOCATION, location)
            }
    }

    private var futureWeatherAdapter = FutureWeatherAdapter(mutableListOf(), this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val recyclerView: RecyclerView = findViewById(R.id.weather_cards)

        val lat = intent.getDoubleExtra(LAT, 0.0)
        val lon = intent.getDoubleExtra(LON, 0.0)
        val location = intent.getStringExtra(LOCATION)

        title = location

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = futureWeatherAdapter

        lifecycleScope.launch {
            futureWeatherAdapter.updateData(getFutureWeather(lat, lon))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
