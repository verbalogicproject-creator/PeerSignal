package com.peersignal.app.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// These were downloadable Google Fonts (Inter / JetBrains Mono) resolved via the
// com.google.android.gms.fonts provider, which is verified against
// res/values/font_certs.xml. That file's certificate was not a real one: its DER
// header declared 1095 bytes over a 1041-byte body, and the "dev" and "prod"
// entries were byte-identical where the genuine file carries two different
// certs. Provider verification failed, Compose threw resolving the first glyph,
// and the app died at launch.
//
// System families need no provider, no Play Services and no network, which fits
// an app whose engine is a local process on loopback. To get Inter back, bundle
// the TTF under res/font and reference it here. Do not reintroduce the
// downloadable-font provider with a hand-written font_certs.xml.
val InterFontFamily = FontFamily.Default
val JetBrainsMonoFamily = FontFamily.Monospace

// Material 3 Typography override
val EditorialTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    labelLarge = TextStyle(
        fontFamily = InterFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    )
)

// Custom Typography for Code
val CodeTypography = TextStyle(
    fontFamily = JetBrainsMonoFamily,
    fontWeight = FontWeight.Normal,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = 0.sp
)
