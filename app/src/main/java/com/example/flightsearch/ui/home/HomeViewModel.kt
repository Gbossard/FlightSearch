package com.example.flightsearch.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flightsearch.data.UserPreferencesRepository
import com.example.flightsearch.data.local.AirportsRepository
import com.example.flightsearch.data.local.FavoritesRepository
import com.example.flightsearch.model.Airport
import com.example.flightsearch.model.Favorite
import com.example.flightsearch.model.FlightDisplay
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchRawUiState(
    val query: String = "",
    val expanded: Boolean = false,
    val selectedAirport: Airport? = null
)

data class SearchUiState(
    val query: String,
    val expanded: Boolean,
    val filteredAirport: List<Airport>,
    val arrivalAirports: List<Airport>,
    val selectedAirport: Airport? = null,
    val favoritesFlights: List<FlightDisplay>
)

class HomeViewModel(
    private val airportsRepository: AirportsRepository,
    private val favoritesRepository: FavoritesRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    private val _searchRawUiState = MutableStateFlow(SearchRawUiState())

    @OptIn(ExperimentalCoroutinesApi::class)
    private val filteredAirportsFlow: Flow<List<Airport>> = _searchRawUiState
        .map { it.query }
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isNotEmpty()) {
                airportsRepository.getAirports(query)
            } else {
                flowOf(emptyList())
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val flightResultsFlow: Flow<List<Airport>> = _searchRawUiState
        .flatMapLatest { rawUiState ->
            rawUiState.selectedAirport?.let {
                airportsRepository.getArrivals(it.iataCode)
            } ?: flowOf(emptyList())
        }

    val allAirportsState: StateFlow<List<Airport>> = airportsRepository
        .getAirports("")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val enrichedFavoritesFlow: Flow<List<FlightDisplay>> = combine(
        favoritesRepository.getAllFavorites(),
        allAirportsState
    ) { favorites, allAirports ->
        val airportByCode = allAirports.associateBy { it.iataCode }

        favorites.map { fav ->
            FlightDisplay(
                departureCode = fav.departureCode,
                departureName = airportByCode[fav.departureCode]?.name ?: "",
                arrivalCode = fav.destinationCode,
                arrivalName = airportByCode[fav.destinationCode]?.name ?: ""
            )
        }
    }

    val searchUiState: StateFlow<SearchUiState> = combine(
        _searchRawUiState,
        filteredAirportsFlow,
        flightResultsFlow,
        enrichedFavoritesFlow
    ) { rawState, filtered, arrivals, favorites ->
        SearchUiState(
            query = rawState.query,
            expanded = rawState.expanded,
            filteredAirport = filtered,
            arrivalAirports = arrivals,
            favoritesFlights = favorites,
            selectedAirport = rawState.selectedAirport,
        )
    } .stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SearchUiState(
            query = "",
            expanded = false,
            filteredAirport = emptyList(),
            arrivalAirports = emptyList(),
            favoritesFlights = emptyList(),
            selectedAirport = null
        )
    )

    init {
        viewModelScope.launch {
            val savedQuery = userPreferencesRepository.searchQuery.first()

            if (savedQuery.isNotEmpty()) {
                _searchRawUiState.update { it.copy(query = savedQuery) }

                val iataCode = savedQuery.take(3)
                val airport = airportsRepository.getAirportByCode(iataCode)

                if (airport != null) {
                    _searchRawUiState.update { it.copy(
                        query = savedQuery,
                        selectedAirport = airport,
                        expanded = false
                    )}
                }
            }
        }
    }

    fun onQueryChange(newQuery: String) {
        _searchRawUiState.update {
            it.copy(query = newQuery, expanded = true, selectedAirport = null)
        }
    }

    fun onResultClick(airport: Airport) {
        _searchRawUiState.update { currentState ->
            currentState.copy(
                query = "${airport.iataCode} — ${airport.name}",
                selectedAirport = airport,
                expanded = false
            )
        }

        viewModelScope.launch {
            userPreferencesRepository.saveSearchQueryPreference("${airport.iataCode} — ${airport.name}")
        }
    }
    fun onSearch(query: String) {
        _searchRawUiState.update { it.copy(expanded = false) }
        viewModelScope.launch {
            userPreferencesRepository.saveSearchQueryPreference(query)
        }
    }

    fun onExpandedChange(expanded: Boolean) {
        _searchRawUiState.update {
            it.copy(expanded = expanded)
        }
    }

    fun onClearQuery() {
        _searchRawUiState.update {
            it.copy(
                query = "",
                selectedAirport = null
            )
        }

        viewModelScope.launch {
            userPreferencesRepository.saveSearchQueryPreference("")
        }
    }

    fun toggleFavorite(departureCode: String, arrivalCode: String) {
        val newFavorite = Favorite(
            departureCode = departureCode,
            destinationCode = arrivalCode
        )
        viewModelScope.launch {
            val currentFavorite = favoritesRepository.getAllFavorites().first()

            val existingFavorite = currentFavorite.find {
                it.departureCode == newFavorite.departureCode && it.destinationCode == newFavorite.destinationCode
            }

            if (existingFavorite != null) {
                favoritesRepository.deleteFavorite(existingFavorite)
            } else {
                favoritesRepository.addFavorite(newFavorite)
            }
        }
    }
}
