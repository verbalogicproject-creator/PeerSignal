package com.peersignal.app.ui.beacon

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.content.Intent
import android.net.Uri
import com.peersignal.app.data.local.entity.BeaconSignalEntity
import com.peersignal.app.theme.EmeraldAccent
import com.peersignal.app.theme.SlateSurface
import com.peersignal.app.ui.components.CodeBlockRenderer

@Composable
fun BeaconStreamScreen(
    onNavigateToConnect: () -> Unit,
    viewModel: BeaconViewModel = hiltViewModel()
) {
    val signals by viewModel.signals.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "PEER SIGNALS",
                style = MaterialTheme.typography.headlineMedium,
                color = EmeraldAccent,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        
        items(signals, key = { it.id }) { signal ->
            BeaconSignalCard(
                signal = signal
            )
        }
        
        if (signals.isEmpty()) {
            item {
                Text(
                    text = "No research signals broadcasted yet. The beacon is silent.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BeaconSignalCard(
    signal: BeaconSignalEntity
) {
    val context = LocalContext.current

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateSurface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = signal.title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = signal.architectureNotes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            if (!signal.codeSnippet.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                CodeBlockRenderer(codeSnippet = signal.codeSnippet)
            }
            
            if (!signal.githubDiscussionUrl.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(signal.githubDiscussionUrl))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldAccent),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(text = "[ Discuss on GitHub ]", color = MaterialTheme.colorScheme.background)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* No URL attached */ },
                    colors = ButtonDefaults.buttonColors(containerColor = SlateSurface),
                    shape = MaterialTheme.shapes.small,
                    enabled = false
                ) {
                    Text(text = "[ No Discussion Link ]", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
