package com.example.weather.ui

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.R
import com.example.weather.models.CurrentWeather
import com.example.weather.databinding.WeatherCardBinding
import com.example.weather.utils.ICONS_BASE_URL
import com.example.weather.utils.ICONS_PNG_FORMAT
import com.example.weather.utils.formatDateTime
import com.example.weather.utils.mapWindSpeedToText

internal class CurrentWeatherAdapter(
    private val onClickListener: (CurrentWeather) -> Unit,
    private val onRemoveListener: (CurrentWeather) -> Unit,
) : ListAdapter<CurrentWeather, CurrentWeatherViewHolder>(CurrentWeatherDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrentWeatherViewHolder {
        val binding = WeatherCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CurrentWeatherViewHolder(binding, onClickListener, onRemoveListener)
    }

    override fun onBindViewHolder(holder: CurrentWeatherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun removeItem(weather: CurrentWeather) {
        val updatedList = currentList.toMutableList()
        updatedList.remove(weather)
        submitList(updatedList)
    }
}

internal class CurrentWeatherViewHolder(
    private val binding: WeatherCardBinding,
    private val onClickListener: (CurrentWeather) -> Unit,
    private val onRemoveListener: (CurrentWeather) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(weather: CurrentWeather) = with(binding) {
        val context = root.context

        Glide.with(context).load("$ICONS_BASE_URL${weather.icon}$ICONS_PNG_FORMAT").into(weatherIcon)

        name.text = weather.name
        description.text = weather.description
        temperature.text = context.getString(R.string.temperature, weather.temperature)
        feelsLike.text = context.getString(R.string.feels_like, weather.feelsLike)
        updateAt.isVisible = true
        updateAt.text = context.getString(R.string.updated_at, formatDateTime(weather.dt))
        wind.text = mapWindSpeedToText(context, weather.windSpeed)
        sunriseSunset.isVisible = true
        sunriseSunset.text = context.getString(R.string.sunrise_sunset, weather.sunrise, weather.sunset)
        deleteIcon.isVisible = true
        deleteIcon.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.removing_location_title))
                .setMessage(
                    context.getString(R.string.removing_location_description, weather.name)
                )
                .setPositiveButton(context.getString(R.string.continue_action)) { _, _ ->
                    onRemoveListener(weather)

                }.setNegativeButton(context.getString(R.string.cancel_action), null).create().show()
        }

        cardView.setOnClickListener { onClickListener(weather) }
    }
}

private class CurrentWeatherDiffCallback : DiffUtil.ItemCallback<CurrentWeather>() {
    override fun areItemsTheSame(oldItem: CurrentWeather, newItem: CurrentWeather) =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: CurrentWeather, newItem: CurrentWeather) =
        oldItem == newItem
}
