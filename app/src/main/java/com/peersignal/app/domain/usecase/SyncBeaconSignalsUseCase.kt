package com.peersignal.app.domain.usecase

import com.peersignal.app.domain.repository.BeaconRepository
import javax.inject.Inject

class SyncBeaconSignalsUseCase @Inject constructor(
    private val repository: BeaconRepository
) {
    suspend operator fun invoke() {
        repository.syncWithRemote()
    }
}
