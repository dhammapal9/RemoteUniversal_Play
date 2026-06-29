package com.hari.androidtvremote.preference

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

// ─── Dark Theme ─────────────────────────────────────────────────────────────

val LocalDarkTheme = compositionLocalOf<DarkThemePreference> { DarkThemePreference.Amoled }

enum class DarkThemePreference(val value: Int) {
    Amoled(3),
    UseDeviceTheme(0),
    Dark(1),
    Light(2);

    @Composable
    @ReadOnlyComposable
    fun isDarkTheme(): Boolean = when (this) {
        Amoled -> true
        UseDeviceTheme -> isSystemInDarkTheme()
        Dark -> true
        Light -> false
    }

    fun isAmoled(): Boolean = this == Amoled

    fun toDisplayName(): String = when (this) {
        Amoled -> "AMOLED"
        UseDeviceTheme -> "System"
        Dark -> "Dark"
        Light -> "Light"
    }

    companion object {
        val default = Amoled
        val values = enumValues<DarkThemePreference>().toList()

        fun fromInt(value: Int): DarkThemePreference = when (value) {
            1 -> Dark
            2 -> Light
            3 -> Amoled
            0 -> UseDeviceTheme
            else -> Amoled
        }
    }
}

@Composable
operator fun DarkThemePreference.not(): DarkThemePreference =
    when (this) {
        DarkThemePreference.Amoled -> DarkThemePreference.Light
        DarkThemePreference.UseDeviceTheme -> if (isSystemInDarkTheme()) DarkThemePreference.Light else DarkThemePreference.Dark
        DarkThemePreference.Dark -> DarkThemePreference.Light
        DarkThemePreference.Light -> DarkThemePreference.Amoled
    }
