package com.example.flightsearch.data.local

import com.example.flightsearch.model.Favorite
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun getAllFavorites(): Flow<List<Favorite>>

    suspend fun addFavorite(favorite: Favorite)

    suspend fun deleteFavorite(favorite: Favorite)
}

class OfflineFavoritesRepository(private val favoriteDao: FavoriteDao) : FavoritesRepository {
    override fun getAllFavorites(): Flow<List<Favorite>> = favoriteDao.getAllFavorites()

    override suspend fun addFavorite(favorite: Favorite) = favoriteDao.addFavorite(favorite)

    override suspend fun deleteFavorite(favorite: Favorite) = favoriteDao.deleteFavorite(favorite)
}