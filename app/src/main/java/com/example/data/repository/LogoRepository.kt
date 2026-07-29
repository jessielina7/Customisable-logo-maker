package com.example.data.repository

import com.example.data.db.LogoDao
import com.example.data.model.LogoEntity
import kotlinx.coroutines.flow.Flow

class LogoRepository(private val logoDao: LogoDao) {
    val allLogos: Flow<List<LogoEntity>> = logoDao.getAllLogos()
    val favoriteLogos: Flow<List<LogoEntity>> = logoDao.getFavoriteLogos()

    suspend fun getLogoById(id: Int): LogoEntity? = logoDao.getLogoById(id)

    suspend fun saveLogo(logo: LogoEntity): Long = logoDao.insertLogo(logo)

    suspend fun deleteLogo(id: Int) = logoDao.deleteLogoById(id)

    suspend fun toggleFavorite(id: Int, isFav: Boolean) = logoDao.updateFavorite(id, isFav)
}
