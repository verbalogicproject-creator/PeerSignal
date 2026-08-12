package com.peersignal.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.peersignal.app.theme.CodeTypography
import com.peersignal.app.theme.EmeraldAccent
import com.peersignal.app.theme.SlateDark
import com.peersignal.app.theme.TextPrimary

@Composable
fun InviteScreen(
    onTokenValidated: () -> Unit
) {
    var token by remember { mutableStateOf("") }
    
    // Minimalist, terminal-like entry. No buttons, just the token input.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "PEER_SIGNAL_OS v1.0.0",
            style = CodeTypography,
            color = EmeraldAccent
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter cryptographic handshake token to access beacon stream.",
            style = CodeTypography,
            color = TextPrimary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "> ",
                style = CodeTypography,
                color = EmeraldAccent
            )
            BasicTextField(
                value = token,
                onValueChange = { newValue ->
                    if (newValue.length <= 12) {
                        token = newValue.uppercase()
                        // Mock validation logic
                        if (token.length == 12) {
                            onTokenValidated()
                        }
                    }
                },
                textStyle = CodeTypography.copy(color = TextPrimary),
                cursorBrush = SolidColor(EmeraldAccent),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    imeAction = ImeAction.Done
                ),
                decorationBox = { innerTextField ->
                    if (token.isEmpty()) {
                        Text(
                            text = "XXXX-XXXX-XXXX",
                            style = CodeTypography,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}
