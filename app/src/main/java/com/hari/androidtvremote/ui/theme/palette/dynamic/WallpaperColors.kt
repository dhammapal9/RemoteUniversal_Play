/**
 * Copyright (C) 2021 Kyant0
 *
 * @link https://github.com/Kyant0/MusicYou
 * @author Kyant0
 * @modifier Ashinch
 */

package com.hari.androidtvremote.ui.theme.palette.dynamic

import android.app.WallpaperManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.hari.androidtvremote.ui.theme.palette.TonalPalettes
import com.hari.androidtvremote.ui.theme.palette.getSystemTonalPalettes
import com.hari.androidtvremote.ui.theme.palette.toTonalPalettes

object PresetColor {
    val blue = Color(0xFF80BBFF)
    val purple = Color(0xFF62539F)
    val pink = Color(0xFFFFD8E4)
    val yellow = Color(0xFFE9B666)
    val green = Color(0xFF0F9D58)
    val red = Color(0xFFDB4437)
    val indigo = Color(0xFF5C6BC0)
    val teal = Color(0xFF26A69A)
    val deepPurple = Color(0xFF673AB7)
    val orange = Color(0xFFFF9800)
    val brown = Color(0xFF795548)
    val blueGrey = Color(0xFF607D8B)
    val pink2 = Color(0xFFE91E63)
    val cyan = Color(0xFF00BCD4)
    val lime = Color(0xFFCDDC39)
    val yellow2 = Color(0xFFFFEB3B)
}

@Composable
@Stable
fun extractTonalPalettesFromUserWallpaper(): List<TonalPalettes> {
    val context = LocalContext.current

    val preset = mutableListOf(
        PresetColor.blue.toTonalPalettes(),
        PresetColor.purple.toTonalPalettes(),
        PresetColor.pink.toTonalPalettes(),
        PresetColor.yellow.toTonalPalettes(),
        PresetColor.green.toTonalPalettes(),
        PresetColor.red.toTonalPalettes(),
        PresetColor.indigo.toTonalPalettes(),
        PresetColor.teal.toTonalPalettes(),
        PresetColor.deepPurple.toTonalPalettes(),
        PresetColor.orange.toTonalPalettes(),
        PresetColor.brown.toTonalPalettes(),
        PresetColor.blueGrey.toTonalPalettes(),
        PresetColor.pink2.toTonalPalettes(),
        PresetColor.cyan.toTonalPalettes(),
        PresetColor.lime.toTonalPalettes(),
        PresetColor.yellow2.toTonalPalettes(),
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1 && !LocalView.current.isInEditMode) {
        val colors = WallpaperManager.getInstance(LocalContext.current)
            .getWallpaperColors(WallpaperManager.FLAG_SYSTEM)
        val primary = colors?.primaryColor?.toArgb()
        val secondary = colors?.secondaryColor?.toArgb()
        val tertiary = colors?.tertiaryColor?.toArgb()
        if (primary != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                preset.add(context.getSystemTonalPalettes())
            } else {
                preset.add(Color(primary).toTonalPalettes())
            }
        }
        if (secondary != null) preset.add(Color(secondary).toTonalPalettes())
        if (tertiary != null) preset.add(Color(tertiary).toTonalPalettes())
    }
    return preset
}
