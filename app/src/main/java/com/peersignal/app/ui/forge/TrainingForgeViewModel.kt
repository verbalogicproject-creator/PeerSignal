package com.peersignal.app.ui.forge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peersignal.app.domain.repository.TrainingRequest
import com.peersignal.app.domain.usecase.TERMINAL_STATUSES
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

    init {
        refreshEngineStatus()
    }

    /**
     * probe() was already written and try/catch-wrapped, but nothing ever
     * called it. Calling it is what lets the UI disable START instead of
     * letting a ConnectException kill the app.
     */
    fun refreshEngineStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(engineState = EngineState.CHECKING)
            val caps = coordinator.probeEngine()
            _uiState.value = _uiState.value.copy(
                engineState = if (caps.available) EngineState.ONLINE else EngineState.OFFLINE,
                engineId = caps.engineId
            )
        }
    }

    fun startTraining() {
        if (_uiState.value.engineState != EngineState.ONLINE) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No engine on 127.0.0.1:8000. Start the companion engine, then RETRY."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(status = "preparing", errorMessage = null)

            val req = TrainingRequest(
                datasetId = "snapshot_001",
                templateId = "MiniULTRA-SKG-v0",
                seed = 42,
                budgetEpochs = BUDGET_EPOCHS
            )

            coordinator.startTraining(req)
                .onFailure { e -> failRun("Could not start run: ${e.message ?: e::class.simpleName}") }
                .onSuccess { runId ->
                    _uiState.value = _uiState.value.copy(runId = runId)
                    coordinator.observeProgress(runId).collect { progress ->
                        val current = _uiState.value
                        // Only append a log line when the epoch actually moves.
                        // Polling at 1Hz against an engine that steps more
                        // slowly was producing duplicate entries for the same
                        // epoch in the terminal view.
                        val logs = if (progress.epoch != current.epoch && progress.epoch > 0) {
                            current.logs + "Epoch ${progress.epoch}: loss ${progress.loss} | Thermal: ${progress.thermalStatus}"
                        } else {
                            current.logs
                        }
                        _uiState.value = current.copy(
                            status = progress.status,
                            epoch = progress.epoch,
                            loss = progress.loss,
                            thermalStatus = progress.thermalStatus,
                            logs = logs,
                            errorMessage = if (progress.status == "failed")
                                "Run failed. The engine stopped responding." else current.errorMessage
                        )
                    }
                }
        }
    }

    fun pauseTraining() {
        val runId = _uiState.value.runId ?: return
        viewModelScope.launch {
            coordinator.pauseTraining(runId)
                .onSuccess { _uiState.value = _uiState.value.copy(status = "paused") }
                .onFailure { e -> failRun("Pause failed: ${e.message ?: e::class.simpleName}") }
        }
    }

    fun resumeTraining() {
        val runId = _uiState.value.runId ?: return
        viewModelScope.launch {
            coordinator.resumeTraining(runId)
                .onSuccess { _uiState.value = _uiState.value.copy(status = "running") }
                .onFailure { e -> failRun("Resume failed: ${e.message ?: e::class.simpleName}") }
        }
    }

    /** Escape hatch from any terminal state. Without this the screen dead-ends. */
    fun reset() {
        _uiState.value = TrainingForgeUiState(
            engineState = _uiState.value.engineState,
            engineId = _uiState.value.engineId
        )
        refreshEngineStatus()
    }

    private fun failRun(message: String) {
        _uiState.value = _uiState.value.copy(status = "failed", errorMessage = message)
    }

    companion object {
        const val BUDGET_EPOCHS = 100
    }
}

enum class EngineState { CHECKING, ONLINE, OFFLINE }

data class TrainingForgeUiState(
    val runId: String? = null,
    val status: String = "idle",
    val epoch: Int = 0,
    val loss: Double = 0.0,
    val thermalStatus: String = "NORMAL",
    val logs: List<String> = emptyList(),
    val engineState: EngineState = EngineState.CHECKING,
    val engineId: String = "",
    val errorMessage: String? = null
) {
    val isTerminal: Boolean get() = status in TERMINAL_STATUSES
}
