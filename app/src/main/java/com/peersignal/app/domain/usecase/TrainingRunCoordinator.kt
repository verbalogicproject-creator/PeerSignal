package com.peersignal.app.domain.usecase

import com.peersignal.app.domain.repository.EngineCapabilities
import com.peersignal.app.domain.repository.SpecialistEngine
import com.peersignal.app.domain.repository.TrainingProgress
import com.peersignal.app.domain.repository.TrainingRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Terminal run states. A run in any of these will never emit again, so the UI
 * must offer a way out of all of them -- previously it offered none, leaving
 * the screen with no controls after any completed or failed run.
 */
val TERMINAL_STATUSES = setOf("completed", "failed", "cancelled")

@Singleton
class TrainingRunCoordinator @Inject constructor(
    // Depends on the interface, not CompanionPythonEngineImpl. The concrete
    // binding is chosen in NetworkModule, so the in-app native engine can
    // replace the loopback one without touching this class.
    private val engine: SpecialistEngine
) {
    suspend fun probeEngine(): EngineCapabilities = engine.probe()

    /**
     * Wrapped in [Result] because the engine may simply not be there. Ktor
     * throws ConnectException when nothing is bound on loopback, and that
     * exception previously escaped viewModelScope and killed the process.
     */
    suspend fun startTraining(request: TrainingRequest): Result<String> =
        runCatching { engine.startRun(request) }

    fun observeProgress(runId: String): Flow<TrainingProgress> = flow {
        var isCompleted = false
        while (!isCompleted) {
            try {
                val progress = engine.getRunProgress(runId)
                emit(progress)
                if (progress.status in TERMINAL_STATUSES) {
                    isCompleted = true
                }
            } catch (e: Exception) {
                emit(TrainingProgress(runId, "failed", 0, 0.0, "UNKNOWN"))
                isCompleted = true
            }
            if (!isCompleted) delay(1000)
        }
    }

    suspend fun pauseTraining(runId: String): Result<Boolean> =
        runCatching { engine.pauseRun(runId) }

    suspend fun resumeTraining(runId: String): Result<Boolean> =
        runCatching { engine.resumeRun(runId) }
}
