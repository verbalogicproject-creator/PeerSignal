package com.peersignal.app.data.remote

import com.peersignal.app.domain.repository.EngineCapabilities
import com.peersignal.app.domain.repository.SpecialistEngine
import com.peersignal.app.domain.repository.TrainingProgress
import com.peersignal.app.domain.repository.TrainingRequest
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CompanionPythonEngineImpl @Inject constructor() : SpecialistEngine {

    override val engineId: String = "CompanionPythonEngine_v1"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }
    
    private val baseUrl = "http://127.0.0.1:8000"

    override suspend fun probe(): EngineCapabilities {
        return try {
            val res = client.get("$baseUrl/v1/capabilities").bodyAsText()
            val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<CapabilitiesResponse>(res)
            EngineCapabilities(
                engineId = parsed.engine_id,
                cpuSupport = parsed.cpu_support,
                gpuSupport = parsed.gpu_support,
                npuSupport = parsed.npu_support,
                supportedTemplates = parsed.supported_templates
            )
        } catch (e: Exception) {
            EngineCapabilities("CompanionPythonEngine_v1", false, false, false, emptyList())
        }
    }

    override suspend fun startRun(request: TrainingRequest): String {
        val payload = RunRequestDto(
            dataset_id = request.datasetId,
            template_id = request.templateId,
            seed = request.seed,
            budget_epochs = request.budgetEpochs
        )
        val res = client.post("$baseUrl/v1/runs") {
            contentType(ContentType.Application.Json)
            setBody(payload)
        }.bodyAsText()
        return Json { ignoreUnknownKeys = true }.decodeFromString<RunResponseDto>(res).run_id
    }

    override suspend fun getRunProgress(runId: String): TrainingProgress {
        val res = client.get("$baseUrl/v1/runs/$runId").bodyAsText()
        val parsed = Json { ignoreUnknownKeys = true }.decodeFromString<RunStatusDto>(res)
        return TrainingProgress(
            runId = parsed.run_id,
            status = parsed.status,
            epoch = parsed.progress.epoch,
            loss = parsed.progress.loss,
            thermalStatus = parsed.progress.thermal_status
        )
    }

    override suspend fun pauseRun(runId: String): Boolean {
        client.post("$baseUrl/v1/runs/$runId/pause")
        return true
    }

    override suspend fun resumeRun(runId: String): Boolean {
        client.post("$baseUrl/v1/runs/$runId/resume")
        return true
    }
}

@Serializable
data class CapabilitiesResponse(
    val engine_id: String,
    val cpu_support: Boolean,
    val gpu_support: Boolean,
    val npu_support: Boolean,
    val supported_templates: List<String>
)

@Serializable
data class RunRequestDto(
    val dataset_id: String,
    val template_id: String,
    val seed: Int,
    val budget_epochs: Int
)

@Serializable
data class RunResponseDto(val run_id: String)

@Serializable
data class RunStatusDto(
    val run_id: String,
    val status: String,
    val progress: ProgressDto
)

@Serializable
data class ProgressDto(
    val epoch: Int,
    val loss: Double,
    val thermal_status: String
)
