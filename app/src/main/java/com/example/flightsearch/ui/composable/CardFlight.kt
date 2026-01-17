package com.example.flightsearch.ui.composable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flightsearch.R

@Composable
fun CardFlight(
    departureAirport: String,
    arrivalAirport: String,
    departureCode: String,
    arrivalCode: String,
    isFavorite: Boolean,
    onClickFavorites: ()-> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(
                modifier = Modifier.weight(2.5f)
            ) {
                Text(
                    text = stringResource(R.string.depart),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
                Row {
                    Text(
                        text = departureCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (departureCode.isNotEmpty()) {
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                    }
                    Text(
                        text = departureAirport,
                        fontWeight = FontWeight.Light,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(
                    text = stringResource(R.string.arrival),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
                Row {
                    Text(
                        text = arrivalCode,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (arrivalCode.isNotEmpty()) {
                        Spacer(
                            modifier = Modifier.width(8.dp)
                        )
                    }
                    Text(
                        text = arrivalAirport,
                        fontWeight = FontWeight.Light,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Icon(
                painter = if (isFavorite) painterResource(R.drawable.rounded_favorite_filled_24) else
                    painterResource(R.drawable.rounded_favorite_outlined_24),
                contentDescription = stringResource(R.string.favorites),
                modifier = Modifier.clickable{
                    onClickFavorites()
                }
            )
        }
    }
}