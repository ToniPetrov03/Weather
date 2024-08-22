package com.example.weather.future_weather

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weather.utils.ICONS_BASE_URL
import com.example.weather.utils.ICONS_PNG_FORMAT
import com.example.weather.R
import com.example.weather.utils.mapWindSpeedToText

internal class FutureWeatherAdapter(
    private var weatherList: MutableList<FutureWeather>,
    private val context: Context
) : RecyclerView.Adapter<FutureWeatherAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.weather_card, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val date: TextView = itemView.findViewById(R.id.date_text)
        val weatherIconImageView: ImageView = itemView.findViewById(R.id.weather_icon)
        val title: TextView = itemView.findViewById(R.id.title_text)
        val weatherDescription: TextView = itemView.findViewById(R.id.weather_description)
        val temperature: TextView = itemView.findViewById(R.id.temperature_text)
        val feelsLike: TextView = itemView.findViewById(R.id.feels_like_text)
        val updatedAt: TextView = itemView.findViewById(R.id.update_at)
        val wind: TextView = itemView.findViewById(R.id.wind_text)
        val chanceOfRain: TextView = itemView.findViewById(R.id.chance_of_rain_text)
        val sunriseSunset: TextView = itemView.findViewById(R.id.sunrise_sunset_text)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val weather = weatherList[position]

        if (weather.date == null) {
            holder.date.visibility = View.GONE
        } else {
            holder.date.text = weather.date
            holder.date.visibility = View.VISIBLE
        }

        Glide.with(context)
            .load("$ICONS_BASE_URL${weather.icon}$ICONS_PNG_FORMAT")
            .error(R.drawable.img_no_image)
            .into(holder.weatherIconImageView)

        holder.title.text = weather.hour
        holder.weatherDescription.text = weather.description
        holder.temperature.text = context.getString(R.string.temperature, weather.temperature)
        holder.feelsLike.text = context.getString(R.string.feels_like, weather.feelsLike)
        holder.wind.text = mapWindSpeedToText(context, weather.windSpeed)
        holder.chanceOfRain.text = context.getString(R.string.chance_of_rain, weather.chanceOfRain)
        holder.updatedAt.visibility = View.GONE
        holder.sunriseSunset.visibility = View.GONE
    }

    override fun getItemCount() = weatherList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateWeather(newWeather: List<FutureWeather>) {
        weatherList.clear()
        weatherList.addAll(newWeather)
        notifyDataSetChanged()
    }
}
