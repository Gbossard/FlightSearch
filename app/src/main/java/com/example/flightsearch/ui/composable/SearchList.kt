package com.example.flightsearch.ui.composable

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.FlightDisplay

@Composable
fun SearchList(
    text: Int,
    query: String,
    departureAirport: Airport,
    arrivalAirports: List<Airport>,
    favoriteFlights: List<FlightDisplay>,
    onClickFavorites: (String, String) -> Unit
) {
    LazyColumn {
        items(count = 1) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(text, query),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(arrivalAirports) { arrival ->
            val isThisFlightFavorite = favoriteFlights.any { favorite ->
                favorite.departureCode == departureAirport.iataCode &&
                        favorite.arrivalCode == arrival.iataCode
            }
            CardFlight(
                departureAirport = departureAirport.name,
                arrivalAirport = arrival.name,
                departureCode = departureAirport.iataCode,
                arrivalCode = arrival.iataCode,
                isFavorite = isThisFlightFavorite,
                onClickFavorites = { onClickFavorites(departureAirport.iataCode, arrival.iataCode) }
            )
        }
    }
}