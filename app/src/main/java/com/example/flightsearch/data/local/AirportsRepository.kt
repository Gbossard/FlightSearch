package com.example.flightsearch.data.local

import com.example.flightsearch.model.Airport
import kotlinx.coroutines.flow.Flow

interface AirportsRepository {
    fun getAirports(query: String): Flow<List<Airport>>

    fun getArrivals(departureCode: String): Flow<List<Airport>>

    suspend fun getAirportByCode(iataCode: String): Airport?
}

class OfflineAirportsRepository(private val airportDao: AirportDao) : AirportsRepository {
    override fun getAirports(query: String): Flow<List<Airport>> = airportDao.getAirports(query)

    override fun getArrivals(departureCode: String): Flow<List<Airport>> = airportDao.getArrivals(departureCode)

    override suspend fun getAirportByCode(iataCode: String): Airport? = airportDao.getAirportByCode(iataCode)
}