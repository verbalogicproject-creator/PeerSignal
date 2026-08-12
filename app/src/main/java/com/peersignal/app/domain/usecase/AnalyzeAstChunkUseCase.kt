package com.peersignal.app.domain.usecase

import com.peersignal.app.data.remote.GeminiApiClient
import javax.inject.Inject

class AnalyzeAstChunkUseCase @Inject constructor(
    private val geminiClient: GeminiApiClient
) {
    suspend operator fun invoke(prompt: String, code: String): String {
        return geminiClient.analyzeCodeChunk(prompt, code)
    }
}
