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
    val engineId: String,
    val cpuSupport: Boolean,
    val gpuSupport: Boolean,
    val npuSupport: Boolean,
    val supportedTemplates: List<String>
)

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
