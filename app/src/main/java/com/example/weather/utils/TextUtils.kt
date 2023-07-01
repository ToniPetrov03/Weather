package com.example.weather.utils

fun capitalize(text: String): String {
    return text.replaceFirstChar { it.uppercaseChar() }
}
