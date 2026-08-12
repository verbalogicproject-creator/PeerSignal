package com.peersignal.app.ui.sandbox

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.peersignal.app.theme.EmeraldAccent
import com.peersignal.app.theme.SlateSurface

@Composable
fun SandboxScreen(
    viewModel: SandboxViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var prompt by remember { mutableStateOf("") }
    var codeSnippet by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "AST & RAG SANDBOX",
            style = MaterialTheme.typography.headlineMedium,
            color = EmeraldAccent
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = prompt,
            onValueChange = { prompt = it },
            label = { Text("Analysis Prompt") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateSurface,
                unfocusedContainerColor = SlateSurface
            )
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = codeSnippet,
            onValueChange = { codeSnippet = it },
            label = { Text("Code Chunk") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = SlateSurface,
                unfocusedContainerColor = SlateSurface
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { viewModel.runRAGAnalysis(prompt, codeSnippet) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
            shape = MaterialTheme.shapes.small,
            enabled = !uiState.isLoading
        ) {
            Text(
                text = if (uiState.isLoading) "ANALYZING..." else "EXECUTE AST PIPELINE",
                color = MaterialTheme.colorScheme.background
            )
        }
        
        if (uiState.result != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "RESULT:",
                style = MaterialTheme.typography.labelLarge,
                color = EmeraldAccent
            )
            Text(
                text = uiState.result!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
