package com.peersignal.app.ui.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.peersignal.app.domain.usecase.GenerateInviteTokenUseCase
import com.peersignal.app.theme.CodeTypography
import com.peersignal.app.theme.EmeraldAccent
import com.peersignal.app.theme.SlateSurface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val generateInviteTokenUseCase: GenerateInviteTokenUseCase
) : ViewModel() {
    private val _generatedToken = MutableStateFlow<String?>(null)
    val generatedToken: StateFlow<String?> = _generatedToken

    fun generateNewToken() {
        _generatedToken.value = generateInviteTokenUseCase.invoke()
    }
}

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val generatedToken by viewModel.generatedToken.collectAsState()
    val clipboardManager = LocalClipboardManager.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "CREATOR IDENTITY",
            style = MaterialTheme.typography.headlineMedium,
            color = EmeraldAccent
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Identity: Verified Developer",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "GitHub: linked\nActive Research: RAG, Clean Architecture, UDF",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "LIMITED DISTRIBUTION CONFIG",
            style = MaterialTheme.typography.titleLarge,
            color = EmeraldAccent
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Manage your 20-device limited access tokens.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { viewModel.generateNewToken() },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
            shape = MaterialTheme.shapes.small,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "GENERATE INVITE TOKEN", color = MaterialTheme.colorScheme.background)
        }
        
        generatedToken?.let { token ->
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "NEW HANDSHAKE TOKEN:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = token,
                        style = CodeTypography,
                        color = EmeraldAccent
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            clipboardManager.setText(AnnotatedString(token)) 
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EmeraldAccent),
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "COPY TO CLIPBOARD")
                    }
                }
            }
        }
    }
}
