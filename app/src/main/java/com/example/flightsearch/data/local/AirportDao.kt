package com.example.flightsearch.data.local

import androidx.room.Dao
import androidx.room.Query
import com.example.flightsearch.model.Airport
import kotlinx.coroutines.flow.Flow

@Dao
interface AirportDao {
    @Query("""
        SELECT * FROM airport
        WHERE name LIKE '%' || :query || '%' OR iata_code LIKE '%' || :query || '%'
        ORDER BY passengers DESC
    """)
    fun getAirports(query: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code != :departureCode")
    fun getArrivals(departureCode: String): Flow<List<Airport>>

    @Query("SELECT * FROM airport WHERE iata_code = :iataCode")
    suspend fun getAirportByCode(iataCode: String): Airport?
}