package com.peersignal.app.ui.sandbox

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peersignal.app.domain.usecase.AnalyzeAstChunkUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SandboxUiState(
    val isLoading: Boolean = false,
    val result: String? = null,
    val error: String? = null
)

@HiltViewModel
class SandboxViewModel @Inject constructor(
    private val analyzeAstChunkUseCase: AnalyzeAstChunkUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SandboxUiState())
    val uiState: StateFlow<SandboxUiState> = _uiState

    fun runRAGAnalysis(prompt: String, codeSnippet: String) {
        viewModelScope.launch {
            _uiState.value = SandboxUiState(isLoading = true)
            try {
                val result = analyzeAstChunkUseCase(prompt, codeSnippet)
                _uiState.value = SandboxUiState(isLoading = false, result = result)
            } catch (e: Exception) {
                _uiState.value = SandboxUiState(isLoading = false, error = e.localizedMessage)
            }
        }
    }
}
