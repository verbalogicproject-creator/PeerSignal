package com.peersignal.app.ui.beacon

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peersignal.app.data.local.entity.BeaconSignalEntity
import com.peersignal.app.domain.repository.BeaconRepository
import com.peersignal.app.domain.usecase.SyncBeaconSignalsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeaconViewModel @Inject constructor(
    private val repository: BeaconRepository,
    private val syncBeaconSignalsUseCase: SyncBeaconSignalsUseCase
) : ViewModel() {

    val signals: StateFlow<List<BeaconSignalEntity>> = repository.getBeaconSignals()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            syncBeaconSignalsUseCase()
        }
    }
}
