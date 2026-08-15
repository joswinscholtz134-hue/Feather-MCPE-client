package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.ModCategory
import com.example.data.model.ModEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ModDao {
    @Query("SELECT * FROM mods ORDER BY name ASC")
    fun getAllMods(): Flow<List<ModEntity>>

    @Query("SELECT * FROM mods WHERE category = :category ORDER BY name ASC")
    fun getModsByCategory(category: ModCategory): Flow<List<ModEntity>>

    @Query("SELECT * FROM mods WHERE isEnabled = 1")
    fun getEnabledMods(): Flow<List<ModEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMods(mods: List<ModEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMod(mod: ModEntity)

    @Update
    suspend fun updateMod(mod: ModEntity)

    @Query("UPDATE mods SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setModEnabled(id: String, isEnabled: Boolean)

    @Query("UPDATE mods SET customValue = :value WHERE id = :id")
    suspend fun setModCustomValue(id: String, value: Float)

    @Query("SELECT COUNT(*) FROM mods")
    suspend fun getModCount(): Int
}
