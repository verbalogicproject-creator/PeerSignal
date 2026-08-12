package com.peersignal.app.domain.repository

import com.peersignal.app.data.local.entity.BeaconSignalEntity
import kotlinx.coroutines.flow.Flow

interface BeaconRepository {
    fun getBeaconSignals(): Flow<List<BeaconSignalEntity>>
    suspend fun addSignal(signal: BeaconSignalEntity)
    suspend fun syncWithRemote()
}
