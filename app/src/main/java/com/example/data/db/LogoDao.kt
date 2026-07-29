package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.LogoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LogoDao {
    @Query("SELECT * FROM saved_logos ORDER BY createdAt DESC")
    fun getAllLogos(): Flow<List<LogoEntity>>

    @Query("SELECT * FROM saved_logos WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteLogos(): Flow<List<LogoEntity>>

    @Query("SELECT * FROM saved_logos WHERE id = :id")
    suspend fun getLogoById(id: Int): LogoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogo(logo: LogoEntity): Long

    @Update
    suspend fun updateLogo(logo: LogoEntity)

    @Query("DELETE FROM saved_logos WHERE id = :id")
    suspend fun deleteLogoById(id: Int)

    @Query("UPDATE saved_logos SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Int, isFavorite: Boolean)
}
