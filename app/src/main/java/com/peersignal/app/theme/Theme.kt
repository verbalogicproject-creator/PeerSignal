package com.peersignal.app.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkEditorialColorScheme = darkColorScheme(
    primary = EmeraldAccent,
    secondary = EmeraldDark,
    tertiary = TextSecondary,
    background = SlateDark,
    surface = SlateSurface,
    surfaceVariant = SlateSurfaceVariant,
    onPrimary = SlateDark,
    onSecondary = SlateDark,
    onTertiary = SlateDark,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = SlateDark
)

@Composable
fun PeerSignalTheme(
    content: @Composable () -> Unit
) {
    // Explicitly NO dynamic color. We enforce the Dark Editorial Theme.
    val colorScheme = DarkEditorialColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = android.graphics.Color.TRANSPARENT
            window.navigationBarColor = android.graphics.Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = EditorialTypography,
        content = content
    )
}
