package com.peersignal.app.data.repository

import com.peersignal.app.data.local.dao.BeaconSignalDao
import com.peersignal.app.data.local.entity.BeaconSignalEntity
import com.peersignal.app.domain.repository.BeaconRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BeaconRepositoryImpl @Inject constructor(
    private val dao: BeaconSignalDao
) : BeaconRepository {

    override fun getBeaconSignals(): Flow<List<BeaconSignalEntity>> {
        return dao.getAllSignals()
    }

    override suspend fun addSignal(signal: BeaconSignalEntity) {
        dao.insertSignal(signal)
        // In a real implementation, also push to Firestore here or trigger a SyncWorker
    }

    override suspend fun syncWithRemote() {
        // Mock remote sync. Pull from Firestore and insert to Room
        // For now, we rely on the local database.
    }
}
