package com.peersignal.app.ui.forge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
private fun RowScope.ForgeButton(
    label: String,
    container: Color,
    content: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = Color(0xFF333333),
            disabledContentColor = Color.Gray
        ),
        shape = RectangleShape,
        modifier = Modifier.weight(1f)
    ) {
        Text(label, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TrainingForgeScreen(
    viewModel: TrainingForgeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        Text(
            text = "TRAINING FORGE",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        // Status Panel
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("STATUS: ${uiState.status.uppercase()}", color = if(uiState.status == "running") Color(0xFF00FF00) else Color(0xFFFFB000), fontFamily = FontFamily.Monospace)
            Text("THERMAL: ${uiState.thermalStatus}", color = if(uiState.thermalStatus == "NORMAL") Color.White else Color(0xFFE22639), fontFamily = FontFamily.Monospace)
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Engine reachability. Without this the only way to discover a missing
        // engine was to tap START and have the process die.
        val engineColor = when (uiState.engineState) {
            EngineState.ONLINE -> Color(0xFF00FF00)
            EngineState.OFFLINE -> Color(0xFFE22639)
            EngineState.CHECKING -> Color(0xFFFFB000)
        }
        Text(
            "ENGINE: ${uiState.engineState.name}",
            color = engineColor,
            fontFamily = FontFamily.Monospace
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "EPOCH: ${uiState.epoch} / ${TrainingForgeViewModel.BUDGET_EPOCHS}",
            color = Color.White,
            fontFamily = FontFamily.Monospace
        )
        Text("LOSS: ${uiState.loss}", color = Color.White, fontFamily = FontFamily.Monospace)

        uiState.errorMessage?.let { message ->
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                color = Color(0xFFE22639),
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Controls. Every reachable status renders exactly one control, so no
        // state can leave the screen without an action.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            when {
                uiState.engineState == EngineState.OFFLINE && uiState.status == "idle" -> {
                    ForgeButton(
                        label = "[ RETRY ENGINE ]",
                        container = Color(0xFFE22639),
                        content = Color.White,
                        onClick = { viewModel.refreshEngineStatus() }
                    )
                }
                uiState.isTerminal -> {
                    ForgeButton(
                        label = "[ RESET ]",
                        container = Color.White,
                        content = Color.Black,
                        onClick = { viewModel.reset() }
                    )
                }
                uiState.status == "idle" -> {
                    ForgeButton(
                        label = "[ START ]",
                        container = Color.White,
                        content = Color.Black,
                        enabled = uiState.engineState == EngineState.ONLINE,
                        onClick = { viewModel.startTraining() }
                    )
                }
                uiState.status == "preparing" -> {
                    ForgeButton(
                        label = "[ PREPARING... ]",
                        container = Color(0xFF333333),
                        content = Color.LightGray,
                        enabled = false,
                        onClick = {}
                    )
                }
                uiState.status == "running" -> {
                    ForgeButton(
                        label = "[ PAUSE ]",
                        container = Color(0xFFFFB000),
                        content = Color.Black,
                        onClick = { viewModel.pauseTraining() }
                    )
                }
                uiState.status == "paused" -> {
                    ForgeButton(
                        label = "[ RESUME ]",
                        container = Color(0xFFE22639),
                        content = Color.White,
                        onClick = { viewModel.resumeTraining() }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Terminal Logs
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFF111111))
                .padding(8.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                items(uiState.logs.reversed()) { log ->
                    Text(
                        text = log,
                        color = Color.LightGray,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }
            }
        }
    }
}
