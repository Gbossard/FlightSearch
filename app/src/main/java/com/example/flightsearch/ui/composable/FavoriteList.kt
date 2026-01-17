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
import com.example.flightsearch.R
import com.example.flightsearch.model.FlightDisplay

@Composable
fun FavoriteList(
    onClickFavorites: (String, String) -> Unit,
    favoriteFlights: List<FlightDisplay>
) {
    LazyColumn {
        item {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(R.string.favorite_routes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        items(favoriteFlights) { flight ->
            CardFlight(
                departureAirport = flight.departureName,
                arrivalAirport = flight.arrivalName,
                departureCode = flight.departureCode,
                arrivalCode = flight.arrivalCode,
                isFavorite = true,
                onClickFavorites = { onClickFavorites(flight.departureCode, flight.arrivalCode) }
            )
        }
    }
}