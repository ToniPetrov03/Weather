package com.example.weather.ui.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.ICONS_BASE_URL
import com.example.weather.ICONS_PNG_FORMAT
import com.example.weather.R
import com.example.weather.network.models.CurrentWeather
import com.example.weather.ui.activities.FutureWeatherActivity
import com.example.weather.utils.Location
import com.example.weather.utils.mapWindSpeedToText
import com.example.weather.utils.removeLocationPreference

internal class CurrentWeatherAdapter(
    private val context: Context,
) : RecyclerView.Adapter<CurrentWeatherAdapter.ViewHolder>() {

    private var locations: MutableList<Location> = mutableListOf()
    private var weatherList: MutableList<CurrentWeather> = mutableListOf()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.weather_card, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val weatherIconImageView: ImageView = itemView.findViewById(R.id.weather_icon)
        val title: TextView = itemView.findViewById(R.id.title_text)
        val weatherDescription: TextView = itemView.findViewById(R.id.weather_description)
        val temperature: TextView = itemView.findViewById(R.id.temperature_text)
        val feelsLike: TextView = itemView.findViewById(R.id.feels_like_text)
        val wind: TextView = itemView.findViewById(R.id.wind_text)
        val chanceOfRain: TextView = itemView.findViewById(R.id.chance_of_rain_text)
        val sunriseSunset: TextView = itemView.findViewById(R.id.sunrise_sunset_text)
        val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherList[position]
        val location = locations[position].name

        Glide.with(context)
            .load("$ICONS_BASE_URL${weather.icon}$ICONS_PNG_FORMAT")
            .error(R.drawable.img_no_image)
            .into(holder.weatherIconImageView)

        holder.title.text = location
        holder.weatherDescription.text = weather.description
        holder.temperature.text = context.getString(R.string.temperature, weather.temperature)
        holder.feelsLike.text = context.getString(R.string.feels_like, weather.feelsLike)
        holder.wind.text = mapWindSpeedToText(weather.windSpeed, context)
        holder.sunriseSunset.text =
            context.getString(R.string.sunrise_sunset, weather.sunrise, weather.sunset)
        holder.chanceOfRain.visibility = View.GONE
        holder.deleteIcon.visibility = View.VISIBLE

        holder.deleteIcon.setOnClickListener {
            showDeleteConfirmationDialog(position)
        }

        holder.itemView.setOnClickListener {
            context.startActivity(
                FutureWeatherActivity.getIntent(
                    context,
                    weather.lat,
                    weather.lon,
                    location
                )
            )
        }
    }

    override fun getItemCount() = weatherList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateWeather(newLocations: List<Location>, newWeather: List<CurrentWeather>) {
        locations.clear()
        locations.addAll(newLocations)
        weatherList.clear()
        weatherList.addAll(newWeather)
        notifyDataSetChanged()
    }

    private fun showDeleteConfirmationDialog(position: Int) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.removing_location_title))
            .setMessage(
                context.getString(R.string.removing_location_description, locations[position].name)
            )
            .setPositiveButton(context.getString(R.string.continue_action)) { _, _ ->
                removeLocation(position)
            }
            .setNegativeButton(context.getString(R.string.cancel_action), null)
            .create()
            .show()
    }

    private fun removeLocation(position: Int) {
        removeLocationPreference(context, position)
        locations.removeAt(position)
        weatherList.removeAt(position)
        notifyItemRemoved(position)
    }
}