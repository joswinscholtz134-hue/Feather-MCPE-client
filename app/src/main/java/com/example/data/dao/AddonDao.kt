package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AddonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AddonDao {
    @Query("SELECT * FROM addons ORDER BY dateAdded DESC")
    fun getAllAddons(): Flow<List<AddonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddon(addon: AddonEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddons(addons: List<AddonEntity>)

    @Update
    suspend fun updateAddon(addon: AddonEntity)

    @Delete
    suspend fun deleteAddon(addon: AddonEntity)

    @Query("DELETE FROM addons WHERE id = :id")
    suspend fun deleteAddonById(id: Long)

    @Query("UPDATE addons SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun setAddonEnabled(id: Long, isEnabled: Boolean)

    @Query("SELECT COUNT(*) FROM addons")
    suspend fun getAddonCount(): Int
}
