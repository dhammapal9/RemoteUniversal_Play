package com.hari.androidtvremote.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Accent palette sampled from the border_color.jpg + nav.jpg references — a
// warm-coral → magenta → violet → cool-blue sweep used across button borders,
// the OK center key and other highlight surfaces in the AMOLED skin.
val AccentCoral = Color(0xFFFF6F4E)
val AccentPinkRed = Color(0xFFFF3E7F)
val AccentMagenta = Color(0xFFE040FB)
val AccentViolet = Color(0xFF8A4DFF)
val AccentBlue = Color(0xFF3E8BFF)
val AccentCyan = Color(0xFF22D3EE)

val AccentBorderBrush: Brush = Brush.linearGradient(
    colors = listOf(
        AccentCoral,
        AccentPinkRed,
        AccentMagenta,
        AccentViolet,
        AccentBlue,
    )
)

val AccentBorderSoftBrush: Brush = Brush.linearGradient(
    colors = listOf(
        AccentCoral.copy(alpha = 0.85f),
        AccentPinkRed.copy(alpha = 0.85f),
        AccentViolet.copy(alpha = 0.85f),
        AccentBlue.copy(alpha = 0.85f),
    )
)

val OkButtonBrush: Brush = Brush.linearGradient(
    colors = listOf(
        AccentCoral,
        AccentPinkRed,
        AccentMagenta,
        AccentBlue,
    )
)

val AccentRingBrush: Brush = Brush.sweepGradient(
    colors = listOf(
        AccentCoral,
        AccentPinkRed,
        AccentMagenta,
        AccentViolet,
        AccentBlue,
        AccentCyan,
        AccentCoral,
    )
)
