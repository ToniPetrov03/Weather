package com.example.weather.current_weather

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
import com.example.weather.R
import com.example.weather.utils.ICONS_BASE_URL
import com.example.weather.utils.ICONS_PNG_FORMAT
import com.example.weather.models.WeatherData
import com.example.weather.future_weather.FutureWeatherActivity
import com.example.weather.utils.formatDateTime
import com.example.weather.utils.mapWindSpeedToText
import com.example.weather.utils.removeWeathersDataPreference

internal class CurrentWeatherAdapter(
    private val context: Context,
) : RecyclerView.Adapter<CurrentWeatherAdapter.ViewHolder>() {

    private var weatherList: MutableList<WeatherData> = mutableListOf()

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
        val updatedAt: TextView = itemView.findViewById(R.id.update_at)
        val wind: TextView = itemView.findViewById(R.id.wind_text)
        val chanceOfRain: TextView = itemView.findViewById(R.id.chance_of_rain_text)
        val sunriseSunset: TextView = itemView.findViewById(R.id.sunrise_sunset_text)
        val deleteIcon: ImageView = itemView.findViewById(R.id.delete_icon)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weatherData = weatherList[position]
        val weather = weatherData.currentWeather

        Glide.with(context)
            .load("$ICONS_BASE_URL${weather?.icon}$ICONS_PNG_FORMAT")
            .error(R.drawable.img_no_image)
            .into(holder.weatherIconImageView)

        holder.title.text = weatherData.name
        holder.weatherDescription.text = weather?.description
        holder.temperature.text = context.getString(R.string.temperature, weather?.temperature)
        holder.feelsLike.text = context.getString(R.string.feels_like, weather?.feelsLike)
        holder.updatedAt.text =
            context.getString(R.string.updated_at, weather?.dt?.let { formatDateTime(it) })
        holder.wind.text = mapWindSpeedToText(context, weather?.windSpeed)
        holder.sunriseSunset.text =
            context.getString(R.string.sunrise_sunset, weather?.sunrise, weather?.sunset)
        holder.chanceOfRain.visibility = View.GONE
        holder.deleteIcon.visibility = View.VISIBLE

        holder.deleteIcon.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.removing_location_title))
                .setMessage(
                    context.getString(R.string.removing_location_description, weatherData.name)
                )
                .setPositiveButton(context.getString(R.string.continue_action)) { _, _ ->
                    removeLocation(weatherData)
                }
                .setNegativeButton(context.getString(R.string.cancel_action), null)
                .create()
                .show()
        }

        holder.itemView.setOnClickListener {
            context.startActivity(
                FutureWeatherActivity.getIntent(context, weatherData.lat, weatherData.lon)
            )
        }
    }

    override fun getItemCount() = weatherList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateWeather(newWeather: List<WeatherData>) {
        weatherList.clear()
        weatherList.addAll(newWeather)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun removeLocation(weatherData: WeatherData) {
        removeWeathersDataPreference(context, weatherData)
        weatherList.remove(weatherData)
        notifyDataSetChanged()
    }
}
