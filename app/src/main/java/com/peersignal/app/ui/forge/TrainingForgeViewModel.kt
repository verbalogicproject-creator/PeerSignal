package com.peersignal.app.ui.forge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peersignal.app.domain.repository.TrainingProgress
import com.peersignal.app.domain.repository.TrainingRequest
import com.peersignal.app.domain.usecase.TrainingRunCoordinator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrainingForgeViewModel @Inject constructor(
    private val coordinator: TrainingRunCoordinator
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrainingForgeUiState())
    val uiState: StateFlow<TrainingForgeUiState> = _uiState.asStateFlow()

    fun startTraining() {
        viewModelScope.launch {
            val req = TrainingRequest(
                datasetId = "snapshot_001",
                templateId = "MiniULTRA-SKG-v0",
                seed = 42,
                budgetEpochs = 100
            )
            val runId = coordinator.startTraining(req)
            _uiState.value = _uiState.value.copy(runId = runId, status = "preparing")
            
            coordinator.observeProgress(runId).collect { progress ->
                _uiState.value = _uiState.value.copy(
                    status = progress.status,
                    epoch = progress.epoch,
                    loss = progress.loss,
                    thermalStatus = progress.thermalStatus,
                    logs = _uiState.value.logs + "Epoch ${progress.epoch}: loss ${progress.loss} | Thermal: ${progress.thermalStatus}"
                )
            }
        }
    }

    fun pauseTraining() {
        viewModelScope.launch {
            val runId = _uiState.value.runId
            if (runId != null) {
                coordinator.pauseTraining(runId)
                _uiState.value = _uiState.value.copy(status = "paused")
            }
        }
    }

    fun resumeTraining() {
        viewModelScope.launch {
            val runId = _uiState.value.runId
            if (runId != null) {
                coordinator.resumeTraining(runId)
                _uiState.value = _uiState.value.copy(status = "running")
            }
        }
    }
}

data class TrainingForgeUiState(
    val runId: String? = null,
    val status: String = "idle",
    val epoch: Int = 0,
    val loss: Double = 0.0,
    val thermalStatus: String = "NORMAL",
    val logs: List<String> = emptyList()
)
