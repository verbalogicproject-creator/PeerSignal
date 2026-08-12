package com.peersignal.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.peersignal.app.data.local.entity.BeaconSignalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BeaconSignalDao {
    @Query("SELECT * FROM beacon_signals ORDER BY timestamp DESC")
    fun getAllSignals(): Flow<List<BeaconSignalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: BeaconSignalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignals(signals: List<BeaconSignalEntity>)

    @Query("DELETE FROM beacon_signals")
    suspend fun clearSignals()
}
