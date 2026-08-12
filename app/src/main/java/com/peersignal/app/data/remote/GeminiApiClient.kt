package com.peersignal.app.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
class GeminiApiClient @Inject constructor() {
    
    // In a real app, this should be injected or fetched from a secure remote config.
    private val apiKey = "YOUR_GEMINI_API_KEY_PLACEHOLDER"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun analyzeCodeChunk(prompt: String, codeSnippet: String): String {
        val url = "http://localhost:8000/api/v1/sandbox/analyze"
        
        // Sending simple JSON payload to FastAPI
        val requestBody = mapOf("code" to "$prompt\n\nCode:\n$codeSnippet")

        // Using basic string parsing for the test
        val responseText: String = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(requestBody)
        }.let {
            it.bodyAsText()
        }
        
        return responseText
    }
}

// Simplified DTOs for Gemini Request/Response
@Serializable
data class GeminiRequest(val contents: List<Content>)

@Serializable
data class Content(val parts: List<Part>)

@Serializable
data class Part(val text: String)

@Serializable
data class GeminiResponse(val candidates: List<Candidate> = emptyList())

@Serializable
data class Candidate(val content: Content)
