package com.peersignal.app

import com.peersignal.app.data.remote.GeminiApiClient
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class ParserApiClientTest {

    @Test
    fun `test connection to local Python proxy`() = runBlocking {
        // Initialize the Ktor client
        val client = GeminiApiClient()
        
        // Send a mock code chunk
        val response = client.analyzeCodeChunk(
            prompt = "Extract AST", 
            codeSnippet = "fun main() { println(\"Hello\") }"
        )
        
        // Output response
        println("Received from Python: $response")
        
        // If the test passes, the Kotlin -> Python pipeline is verified!
        assertTrue("Response should contain success message from Python", response.contains("success"))
    }
}
