package com.hari.androidtvremote.ui.app

import android.app.Activity
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hari.androidtvremote.preference.AppearanceKeys
import com.hari.androidtvremote.preference.DarkThemePreference
import com.hari.androidtvremote.preference.LocalDarkTheme
import com.hari.androidtvremote.preference.LocalThemeIndex
import com.hari.androidtvremote.preference.appearanceDataStore
import com.hari.androidtvremote.ui.theme.AccentBorderBrush
import com.hari.androidtvremote.ui.theme.palette.TonalPalettes
import com.hari.androidtvremote.ui.theme.palette.dynamic.extractTonalPalettesFromUserWallpaper
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }
    val scope = rememberCoroutineScope()
    val currentDarkTheme = LocalDarkTheme.current
    val themeIndex = LocalThemeIndex.current
    val palettes = extractTonalPalettesFromUserWallpaper()
    val basicPalettes = palettes.take(BASIC_PALETTE_COUNT)
    val wallpaperPalettes = palettes.drop(BASIC_PALETTE_COUNT)
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    fun updateThemeIndex(index: Int) {
        scope.launch {
            context.appearanceDataStore.edit { prefs ->
                prefs[AppearanceKeys.THEME_INDEX] = index
            }
        }
    }

    fun updateDarkTheme(pref: DarkThemePreference) {
        scope.launch {
            context.appearanceDataStore.edit { prefs ->
                prefs[AppearanceKeys.DARK_THEME] = pref.value
            }
        }
    }

    AppBackdrop {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                SettingsTopBar(
                    title = "Appearance",
                    onBack = onBack,
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                AdmobBanner(modifier = Modifier.padding(bottom = 8.dp))
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {


                item {
                    SettingSubtitle(text = "Basic Colors")
                }
                item {
                    PaletteRow(
                        palettes = basicPalettes,
                        selectedIndex = themeIndex,
                        indexOffset = 0,
                        emptyMessage = "Basic colors are unavailable right now.",
                        onSelect = ::updateThemeIndex
                    )
                }
                item {
                    SettingSubtitle(text = "Wallpaper Colors")
                }
                item {
                    PaletteRow(
                        palettes = wallpaperPalettes,
                        selectedIndex = themeIndex,
                        indexOffset = BASIC_PALETTE_COUNT,
                        emptyMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                            "No wallpaper colors were detected on this device."
                        } else {
                            "Wallpaper colors require Android 8.1 or newer."
                        },
                        onSelect = ::updateThemeIndex
                    )
                }
                item {
                    SettingSubtitle(text = "Theme Mode")
                }
                item {
                    ThemeModeSelector(
                        current = currentDarkTheme,
                        onSelect = ::updateDarkTheme
                    )
                }
            }
        }
    }
}

@Composable
private fun PaletteRow(
    palettes: List<TonalPalettes>,
    selectedIndex: Int,
    indexOffset: Int,
    emptyMessage: String,
    onSelect: (Int) -> Unit,
) {
    if (palettes.isEmpty()) {
        Row(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = emptyMessage,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            palettes.forEachIndexed { index, palette ->
                PaletteChip(
                    selected = selectedIndex == indexOffset + index,
                    palette = palette,
                    onClick = { onSelect(indexOffset + index) }
                )
            }
        }
    }
}

@Composable
private fun PaletteChip(
    selected: Boolean,
    palette: TonalPalettes,
    onClick: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceContainerLow
    ) {
        Surface(
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(16.dp)
                .size(48.dp),
            shape = CircleShape,
            color = palette primary 90,
        ) {
            Box {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .offset((-24).dp, 24.dp),
                    color = palette tertiary 90,
                ) {}
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .offset(24.dp, 24.dp),
                    color = palette secondary 60,
                ) {}
                AnimatedVisibility(
                    visible = selected,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    enter = fadeIn() + expandIn(expandFrom = Alignment.Center),
                    exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.surface
                    )
                }
            }
        }
    }
}

private const val BASIC_PALETTE_COUNT = 4

@Composable
private fun ThemeModeSelector(
    current: DarkThemePreference,
    onSelect: (DarkThemePreference) -> Unit,
) {
    val options = listOf(
        ThemeModeOption(DarkThemePreference.Amoled, "AMOLED", Icons.Outlined.AutoAwesome),
        ThemeModeOption(DarkThemePreference.UseDeviceTheme, "System", Icons.Outlined.PhoneAndroid),
        ThemeModeOption(DarkThemePreference.Dark, "Dark", Icons.Outlined.DarkMode),
        ThemeModeOption(DarkThemePreference.Light, "Light", Icons.Outlined.LightMode),
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        options.forEach { option ->
            ThemeModeChip(
                modifier = Modifier.weight(1f),
                label = option.label,
                icon = option.icon,
                selected = current == option.pref,
                onClick = { onSelect(option.pref) }
            )
        }
    }
}

@Composable
private fun ThemeModeChip(
    modifier: Modifier = Modifier,
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(18.dp)
    val container = if (selected) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainerLow
    }
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(shape)
            .background(container)
            .then(
                if (selected) {
                    Modifier.border(width = 1.5.dp, brush = AccentBorderBrush, shape = shape)
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f),
                        shape = shape
                    )
                }
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp),
                tint = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

private data class ThemeModeOption(
    val pref: DarkThemePreference,
    val label: String,
    val icon: ImageVector,
)
