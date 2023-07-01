package com.example.weather.utils

import android.view.View

fun toggleVisibility(vararg views: View) {
    views.forEach {
        it.visibility = when (it.visibility) {
            View.GONE -> View.VISIBLE
            View.VISIBLE -> View.GONE
            else -> View.GONE
        }
    }
}
