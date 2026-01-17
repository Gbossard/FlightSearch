package com.example.flightsearch.ui.navigation

import com.example.flightsearch.R

sealed class Screen(
    val routes: String,
    val titleRes: Int
) {
    data object Home: Screen("/home", R.string.app_name)
}