package com.example.weather.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.models.FutureWeather
import com.example.weather.databinding.WeatherCardBinding
import com.example.weather.utils.ICONS_BASE_URL
import com.example.weather.utils.ICONS_PNG_FORMAT
import com.example.weather.utils.mapWindSpeedToText

internal class FutureWeatherAdapter : ListAdapter<FutureWeather, FutureWeatherViewHolder>(FutureWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureWeatherViewHolder {
        val binding = WeatherCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FutureWeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FutureWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

internal class FutureWeatherViewHolder(
    private val binding: WeatherCardBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(weather: FutureWeather) = with(binding) {
        val context = root.context

        Glide.with(context)
            .load("$ICONS_BASE_URL${weather.icon}$ICONS_PNG_FORMAT")
            .into(weatherIcon)

        date.text = weather.date
        date.isVisible = weather.date != null
        name.text = weather.hour
        description.text = weather.description
        temperature.text = context.getString(R.string.temperature, weather.temperature)
        feelsLike.text = context.getString(R.string.feels_like, weather.feelsLike)
        wind.text = mapWindSpeedToText(context, weather.windSpeed)
        chanceOfRain.isVisible = true
        chanceOfRain.text = context.getString(R.string.chance_of_rain, weather.chanceOfRain)
    }
}

private class FutureWeatherDiffCallback : DiffUtil.ItemCallback<FutureWeather>() {
    override fun areItemsTheSame(oldItem: FutureWeather, newItem: FutureWeather) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: FutureWeather, newItem: FutureWeather) =
        oldItem == newItem
}
