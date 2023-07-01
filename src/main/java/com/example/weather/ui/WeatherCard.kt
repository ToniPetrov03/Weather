package com.example.weather.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.weather.R
import com.example.weather.api.WeatherAPI

@Composable
fun WeatherCard(
    icon: String,
    name: String,
    description: String,
    temperature: Int,
    feelsLike: Int,
    info1: String,
    info2: String,
    info3: String,
    onClick: (() -> Unit)? = null,
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                AsyncImage(
                    model = WeatherAPI.getIconURL(icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(context.getColor(R.color.sky_blue)))
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = description,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = context.getString(R.string.temperature, temperature),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = context.getString(R.string.feels_like, feelsLike),
                        fontSize = 11.sp
                    )
                }
            }

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = info1,
                fontSize = 16.sp
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = info2,
                fontSize = 16.sp
            )

            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = info3,
                fontSize = 16.sp
            )
        }
    }
}
