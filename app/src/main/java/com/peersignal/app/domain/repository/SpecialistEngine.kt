package com.peersignal.app.domain.repository

import kotlinx.coroutines.flow.Flow

interface SpecialistEngine {
    val engineId: String

    suspend fun probe(): EngineCapabilities
    
    // Simplification for MVP
    suspend fun startRun(request: TrainingRequest): String
    
    suspend fun getRunProgress(runId: String): TrainingProgress
    
    suspend fun pauseRun(runId: String): Boolean
    
    suspend fun resumeRun(runId: String): Boolean
}

data class EngineCapabilities(
    /**
     * False when the engine could not be reached at all.
     *
     * Without this, a failed probe was indistinguishable from a live engine
     * that merely reported no CPU/GPU/NPU support, so the UI had no honest way
     * to say "offline".
     */
    val available: Boolean,
    val engineId: String,
    val cpuSupport: Boolean,
    val gpuSupport: Boolean,
    val npuSupport: Boolean,
    val supportedTemplates: List<String>
) {
    companion object {
        fun unavailable(engineId: String) = EngineCapabilities(
            available = false,
            engineId = engineId,
            cpuSupport = false,
            gpuSupport = false,
            npuSupport = false,
            supportedTemplates = emptyList()
        )
    }
}

data class TrainingRequest(
    val datasetId: String,
    val templateId: String,
    val seed: Int,
    val budgetEpochs: Int
)

data class TrainingProgress(
    val runId: String,
    val status: String,
    val epoch: Int,
    val loss: Double,
    val thermalStatus: String
)
