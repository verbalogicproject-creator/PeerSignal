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
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("EPOCH: ${uiState.epoch} / 100", color = Color.White, fontFamily = FontFamily.Monospace)
        Text("LOSS: ${uiState.loss}", color = Color.White, fontFamily = FontFamily.Monospace)

        Spacer(modifier = Modifier.height(32.dp))

        // Controls
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            if (uiState.status == "idle") {
                Button(
                    onClick = { viewModel.startTraining() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("[ START ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            } else if (uiState.status == "running") {
                Button(
                    onClick = { viewModel.pauseTraining() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB000), contentColor = Color.Black),
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("[ PAUSE ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                }
            } else if (uiState.status == "paused") {
                Button(
                    onClick = { viewModel.resumeTraining() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE22639), contentColor = Color.White),
                    shape = RectangleShape,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("[ RESUME ]", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
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
