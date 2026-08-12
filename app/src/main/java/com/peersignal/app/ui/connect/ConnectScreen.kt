package com.peersignal.app.ui.connect

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.peersignal.app.theme.EmeraldAccent

@Composable
fun ConnectScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "PEER CONNECTION BRIDGE",
            style = MaterialTheme.typography.headlineMedium,
            color = EmeraldAccent
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Direct Collaboration Requests",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Initiate structured 1-on-1 peer discussions attached directly to a specific code snippet. Deep-links to GitHub Discussions or WebRTC channels will appear here.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
