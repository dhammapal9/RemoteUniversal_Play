package com.hari.androidtvremote.ui.app

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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.edit
import com.hari.androidtvremote.preference.AppearanceKeys
import com.hari.androidtvremote.preference.DarkThemePreference
import com.hari.androidtvremote.preference.LocalDarkTheme
import com.hari.androidtvremote.preference.LocalThemeIndex
import com.hari.androidtvremote.preference.appearanceDataStore
import com.hari.androidtvremote.ui.theme.palette.TonalPalettes
import com.hari.androidtvremote.ui.theme.palette.toTonalPalettes
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceScreen(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentDarkTheme = LocalDarkTheme.current
    val themeIndex = LocalThemeIndex.current

    val basicColors = remember {
        listOf(
            Color(0xFF80BBFF), // Blue
            Color(0xFF62539F), // Purple
            Color(0xFFFFD8E4), // Pink
            Color(0xFFE9B666), // Yellow
            Color(0xFF0F9D58), // Green
            Color(0xFFDB4437), // Red
            Color(0xFF5C6BC0), // Indigo
            Color(0xFF26A69A), // Teal
            Color(0xFF673AB7), // Deep Purple
            Color(0xFFFF9800), // Orange
            Color(0xFF795548), // Brown
            Color(0xFF607D8B), // Blue Grey
            Color(0xFFE91E63), // Pink 2
            Color(0xFF00BCD4), // Cyan
            Color(0xFFCDDC39), // Lime
            Color(0xFFFFEB3B), // Yellow 2
        )
    }

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
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    SettingsSection(title = "App Theme") {
                        ThemeModeSelector(
                            current = currentDarkTheme,
                            onSelect = ::updateDarkTheme
                        )
                    }
                }

                item {
                    SettingsSection(title = "Theme Colors") {
                        Column(modifier = Modifier.padding(bottom = 16.dp)) {
                            Text(
                                text = "Select a color to personalize your remote experience.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(16.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                basicColors.forEachIndexed { index: Int, color: Color ->
                                    ColorDot(
                                        selected = themeIndex == index,
                                        color = color,
                                        onClick = { updateThemeIndex(index) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ColorDot(
    selected: Boolean,
    color: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) color else Color.Transparent,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.size(32.dp),
            shape = CircleShape,
            color = color,
            shadowElevation = if (selected) 4.dp else 0.dp
        ) {
            if (selected) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = "Selected",
                        tint = if (color.luminance() > 0.5f) Color.Black else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

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
            .padding(16.dp),
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
    val shape = RoundedCornerShape(20.dp)
    val container = if (selected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
    }
    Box(
        modifier = modifier
            .height(84.dp)
            .clip(shape)
            .background(container)
            .then(
                if (selected) {
                    Modifier.border(width = 1.dp, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), shape = shape)
                } else {
                    Modifier
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
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
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
