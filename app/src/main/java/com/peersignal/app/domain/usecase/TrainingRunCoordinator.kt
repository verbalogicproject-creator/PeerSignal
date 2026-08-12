package com.peersignal.app.domain.usecase

import com.peersignal.app.data.remote.CompanionPythonEngineImpl
import com.peersignal.app.domain.repository.TrainingProgress
import com.peersignal.app.domain.repository.TrainingRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrainingRunCoordinator @Inject constructor(
    private val engine: CompanionPythonEngineImpl
) {
    suspend fun startTraining(request: TrainingRequest): String {
        return engine.startRun(request)
    }

    fun observeProgress(runId: String): Flow<TrainingProgress> = flow {
        var isCompleted = false
        while (!isCompleted) {
            try {
                val progress = engine.getRunProgress(runId)
                emit(progress)
                if (progress.status == "completed" || progress.status == "failed" || progress.status == "cancelled") {
                    isCompleted = true
                }
            } catch (e: Exception) {
                // If engine dies, emit a failure state
                emit(TrainingProgress(runId, "failed", 0, 0.0, "UNKNOWN"))
                isCompleted = true
            }
            delay(1000) // Poll every second for MVP
        }
    }

    suspend fun pauseTraining(runId: String): Boolean {
        return engine.pauseRun(runId)
    }

    suspend fun resumeTraining(runId: String): Boolean {
        return engine.resumeRun(runId)
    }
}
