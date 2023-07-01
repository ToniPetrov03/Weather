package com.example.weather.ui.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.R
import com.example.weather.network.models.CurrentWeather
import com.example.weather.network.responses.getCurrentWeather
import com.example.weather.ui.adapters.CurrentWeatherAdapter
import com.example.weather.utils.Location
import com.example.weather.utils.hasInternetConnectivity
import com.example.weather.utils.readLocationPreferences
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue

internal class CurrentWeatherActivity : AppCompatActivity() {
    private val weatherAdapter = CurrentWeatherAdapter(this)

    private val addLocationActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                fetchCurrentWeather(readLocationPreferences(this))
            }
        }

    private var locationsData: List<Location>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_activity)

        val recyclerView: RecyclerView = findViewById(R.id.weather_cards)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = weatherAdapter

        fun init(dialogInterface: DialogInterface?) {
            dialogInterface?.apply { dismiss() }
            if (hasInternetConnectivity(this)) {
                readLocationPreferences(this).let {
                    locationsData = it
                    fetchCurrentWeather(it)
                }
            } else {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.no_internet_connection_title))
                    .setMessage(getString(R.string.no_internet_connection_description))
                    .setPositiveButton(getString(R.string.reload)) { dialog, _ -> init(dialog) }
                    .setCancelable(false)
                    .show()
            }
        }

        init(null)
    }

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
                        locationsData?.lastOrNull()?.lat,
                        locationsData?.lastOrNull()?.lon
                    )
                )
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fetchCurrentWeather(locations: List<Location>) {
        locations.takeIf {
            lifecycleScope.launch {
                val weatherQueue = ConcurrentLinkedQueue<CurrentWeather>()

                for (location in it) {
                    weatherQueue.add(getCurrentWeather(location.lat, location.lon))
                }

                weatherAdapter.updateWeather(it, weatherQueue.toList())
            }
            it.isNotEmpty()
        }
    }
}
