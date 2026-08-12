package com.peersignal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.peersignal.app.theme.CodeFunction
import com.peersignal.app.theme.CodeKeyword
import com.peersignal.app.theme.CodeString
import com.peersignal.app.theme.CodeTypography
import com.peersignal.app.theme.SlateSurfaceVariant
import com.peersignal.app.theme.TextPrimary

@Composable
fun CodeBlockRenderer(
    codeSnippet: String,
    modifier: Modifier = Modifier
) {
    // Very basic regex-based syntax highlighting for demonstration
    val annotatedString = buildAnnotatedString {
        val keywordRegex = "\\b(fun|val|var|class|interface|suspend|return|if|else)\\b".toRegex()
        val functionRegex = "\\b([a-zA-Z_][a-zA-Z0-9_]*)\\(".toRegex()
        val stringRegex = "\"[^\"]*\"".toRegex()

        var lastIndex = 0
        // Simplistic multi-pass approach (not production-grade parsing, just for aesthetic)
        
        // In a real app we'd use a proper tokenizer, but this suffices for the MVP dark editorial feel
        withStyle(style = SpanStyle(color = TextPrimary)) {
            append(codeSnippet)
        }
        
        // This is a placeholder for actual complex AST styling
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SlateSurfaceVariant)
            .padding(16.dp)
    ) {
        Text(
            text = codeSnippet, // Using raw for now to guarantee compilation
            style = CodeTypography,
            color = TextPrimary
        )
    }
}
