package com.example.flightsearch.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.flightsearch.data.local.AirportsRepository
import com.example.flightsearch.data.local.FavoritesRepository
import com.example.flightsearch.data.local.FlightDatabase
import com.example.flightsearch.data.local.OfflineAirportsRepository
import com.example.flightsearch.data.local.OfflineFavoritesRepository

interface AppContainer {
    val airportsRepository: AirportsRepository
    val favoritesRepository: FavoritesRepository
    val userPreferencesRepository: UserPreferencesRepository
}

class DefaultAppContainer(private val context: Context, dataStore: DataStore<Preferences>) : AppContainer {
    override val airportsRepository: AirportsRepository by lazy {
        OfflineAirportsRepository(FlightDatabase.getDatabase(context).airportDao())
    }

    override val favoritesRepository: FavoritesRepository by lazy {
        OfflineFavoritesRepository(FlightDatabase.getDatabase(context).favoriteDao())
    }

    override val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(dataStore)
    }
}