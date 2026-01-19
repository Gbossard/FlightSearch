package com.example.flightsearch.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarDefaults.inputFieldColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flightsearch.R
import com.example.flightsearch.model.Airport
import com.example.flightsearch.ui.AppViewModelProvider
import com.example.flightsearch.ui.composable.AutoSuggestionItem
import com.example.flightsearch.ui.composable.ErrorFlight
import com.example.flightsearch.ui.composable.FavoriteList
import com.example.flightsearch.ui.composable.SearchList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    HomeBody(
        modifier = modifier.fillMaxSize()
    )
}


@Composable
fun HomeBody(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val searchUiState by viewModel.searchUiState.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(0.dp),
    ) {
        CustomizableSearchBar(
            query = searchUiState.query,
            onQueryChange = viewModel::onQueryChange,
            onSearch = viewModel::onSearch,
            searchResults = searchUiState.filteredAirport,
            onResultClick = viewModel::onResultClick,
            expanded = searchUiState.expanded,
            onExpandedChange = viewModel::onExpandedChange,
            // Customize appearance with optional parameters
            placeholder = { Text(stringResource(R.string.search_departure_airport)) },
            leadingIcon = {
                if (searchUiState.query.isNotEmpty() || searchUiState.expanded) {
                    IconButton(onClick = {
                        viewModel.onClearQuery()
                        viewModel.onExpandedChange(false)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_arrow_back_24),
                            contentDescription = stringResource(R.string.back_button)
                        )
                    }
                } else {
                    Icon(
                        painter = painterResource(R.drawable.rounded_search_24),
                        contentDescription = stringResource(R.string.search)
                    )
                }

            },
            trailingIcon = {
                if (searchUiState.query.isNotEmpty()) {
                    IconButton(onClick = {
                        viewModel.onClearQuery()
                        viewModel.onExpandedChange(true)
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.rounded_close_24),
                            contentDescription = stringResource(R.string.close)
                        )
                    }
                }
            },
        )

        when {
            searchUiState.query.isEmpty() -> {
                if (searchUiState.favoritesFlights.isNotEmpty()) {
                    FavoriteList(
                        onClickFavorites = viewModel::toggleFavorite,
                        favoriteFlights = searchUiState.favoritesFlights
                    )
                }
            }
            searchUiState.selectedAirport != null -> {
                SearchList(
                    text = R.string.flight_from,
                    query = searchUiState.query,
                    departureAirport = searchUiState.selectedAirport!!,
                    arrivalAirports = searchUiState.arrivalAirports,
                    onClickFavorites = { dep, arr -> viewModel.toggleFavorite(dep, arr) },
                    favoriteFlights = searchUiState.favoritesFlights
                )
            }
            searchUiState.selectedAirport == null && searchUiState.query.isNotEmpty() -> {
                ErrorFlight(query = searchUiState.query)
            }
            !searchUiState.expanded && searchUiState.filteredAirport.isEmpty() -> {
                ErrorFlight(query = searchUiState.query)
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomizableSearchBar(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    searchResults: List<Airport>,
    onResultClick: (Airport) -> Unit,
    // Customization options
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
            .fillMaxWidth()
            .semantics { isTraversalGroup = true },
    ) {
        SearchBar(
            modifier = Modifier
                .padding(if (expanded) 0.dp else 16.dp)
                .fillMaxWidth()
                .semantics { traversalIndex = 0f },
            colors = SearchBarDefaults.colors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            windowInsets = WindowInsets(0.dp),
            inputField = {
                // Customizable input field implementation
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = onQueryChange,
                    onSearch = {
                        onSearch(query)
                        onExpandedChange(false)
                    },
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    placeholder = placeholder,
                    leadingIcon = leadingIcon,
                    trailingIcon = trailingIcon,
                    colors =  inputFieldColors(
                        focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            },
            expanded = expanded,
            onExpandedChange = onExpandedChange,
        ) {
            // Show search results in a lazy column for better performance
            if (query.isNotEmpty()) {
                LazyColumn {
                    items(searchResults) { airport ->
                        AutoSuggestionItem(
                            painter = R.drawable.rounded_flight_24,
                            painterDescription = R.string.flight,
                            resultText = "${airport.iataCode} — ${airport.name}",
                            onClick = {
                                onResultClick(airport)
                                onExpandedChange(false)
                            },
                        )
                    }
                }
            }
        }
    }
}