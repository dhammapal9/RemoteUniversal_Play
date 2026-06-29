package com.hari.androidtvremote.ui.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Cast
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Apps
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.hari.androidtvremote.BuildConfig
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import com.hari.androidtvremote.androidLib.remote.Remotemessage
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import kotlinx.coroutines.launch

// Outline icons for unselected state — ReadYou uses outlined/filled toggle
private val HomeTab.outlinedIcon: ImageVector
    get() = when (this) {
        HomeTab.Remote -> Icons.Outlined.Tv
        HomeTab.Apps -> Icons.Outlined.GridView
        HomeTab.Cast -> Icons.Outlined.Cast
        HomeTab.Discover -> Icons.Outlined.Search
        HomeTab.Settings -> Icons.Outlined.Settings
    }

private val HomeTab.filledIcon: ImageVector
    get() = when (this) {
        HomeTab.Remote -> Icons.Filled.Tv
        HomeTab.Apps -> Icons.Filled.GridView
        HomeTab.Cast -> Icons.Filled.Cast
        HomeTab.Discover -> Icons.Filled.Search
        HomeTab.Settings -> Icons.Filled.Settings
    }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentTab: HomeTab,
    activePadMode: RemotePadMode,
    defaultPadMode: RemotePadMode,
    sessionState: TvRemoteUiState,
    hapticsEnabled: Boolean,
    remoteApps: List<RemoteShortcutApp>,
    onTabSelected: (HomeTab) -> Unit,
    onCyclePadMode: () -> Unit,
    onSetPadMode: (RemotePadMode) -> Unit = {},
    onOpenDiscovery: () -> Unit,
    onOpenSettings: () -> Unit,
    onPower: () -> Unit,
    onQuickApp: (String) -> Unit,
    onRemoteKey: (Remotemessage.RemoteKeyCode) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit,
    onKeyboardText: (String) -> Unit,
    onKeyboardBackspace: (Int) -> Unit,
    onKeyboardEnter: () -> Unit,
    onToggleVoice: () -> Unit,
    onOpenCastPlayer: (MediaItemUi) -> Unit,
    onUserRated: () -> Unit = {},
    onUserFeedbackClicked: () -> Unit = {},
    onDismissRatingPrompt: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val haptics = LocalHapticFeedback.current
    var showKeyboardDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(sessionState.statusMessage, sessionState.isError) {
        val message = sessionState.statusMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
    }

    fun performHaptic() {
        if (hapticsEnabled) {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    fun handleAction(action: () -> Unit) {
        performHaptic()
        action()
    }

    fun handleRemoteAction(action: () -> Unit) {
        performHaptic()
        if (sessionState.connectedDevice != null) {
            action()
        } else {
            onOpenDiscovery()
        }
    }

    AppBackdrop {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                CenterAlignedTopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    navigationIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { handleRemoteAction { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_HOME) } }
                            ) {
                                Icon(Icons.Filled.Home, contentDescription = "Home")
                            }
                            IconButton(
                                onClick = { handleRemoteAction { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_VOLUME_MUTE) } }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeMute, contentDescription = "Mute")
                            }
                        }
                    },
                    title = {
                        if (currentTab == HomeTab.Remote) {
                            Surface(
                                shape = RoundedCornerShape(24.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f),
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    RemotePadMode.entries.forEach { mode ->
                                        val isSelected = activePadMode == mode
                                        val bgColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent
                                        val iconColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(bgColor)
                                                .clickable { handleAction { onSetPadMode(mode) } },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = mode.icon,
                                                contentDescription = mode.label,
                                                tint = iconColor,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                if (sessionState.connectedDevice != null) {
                                    showKeyboardDialog = true
                                } else {
                                    onOpenDiscovery()
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Keyboard, contentDescription = "Keyboard")
                        }
                        IconButton(
                            onClick = { handleRemoteAction(onPower) }
                        ) {
                            Icon(Icons.Filled.PowerSettingsNew, contentDescription = "Power")
                        }
                    }
                )
            },
            bottomBar = {
                // ── ReadYou-style bottom nav ──
                Column {
                    AdmobBanner(modifier = Modifier.padding(bottom = 8.dp))
                    HorizontalDivider(
                        modifier = Modifier.fillMaxWidth(),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                    )
                    NavigationBar(
                        modifier = Modifier.navigationBarsPadding(),
                        containerColor = Color.Transparent,   // transparent — backdrop shows through
                        tonalElevation = 0.dp
                    ) {
                        HomeTab.entries.forEach { tab ->
                            val selected = currentTab == tab
                            val iconColor = animateColorAsState(
                                targetValue = if (selected)
                                    MaterialTheme.colorScheme.onSecondaryContainer
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                animationSpec = tween(220),
                                label = "iconColor"
                            )

                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    when (tab) {
                                        HomeTab.Settings -> onOpenSettings()
                                        HomeTab.Discover -> onOpenDiscovery()
                                        else -> onTabSelected(tab)
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) tab.filledIcon else tab.outlinedIcon,
                                        contentDescription = tab.label,
                                        tint = iconColor.value,
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = tab.label,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    // pill indicator — matches ReadYou's secondaryContainer pill
                                    indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                                    selectedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Crossfade(targetState = currentTab, label = "tabContent") { tab ->
                when (tab) {
                    HomeTab.Remote -> RemoteScreen(
                        modifier = Modifier.padding(innerPadding),
                        activePadMode = activePadMode,
                        defaultPadMode = defaultPadMode,
                        sessionState = sessionState,
                        onRequireConnection = onOpenDiscovery,
                        onCyclePadMode = onCyclePadMode,
                        onRemoteKey = { key -> handleRemoteAction { onRemoteKey(key) } },
                        onVolumeUp = { handleRemoteAction(onVolumeUp) },
                        onVolumeDown = { handleRemoteAction(onVolumeDown) },
                        onToggleVoice = { handleRemoteAction(onToggleVoice) }
                    )

                    HomeTab.Apps -> AppsScreen(
                        modifier = Modifier.padding(innerPadding),
                        quickApps = remoteApps,
                        onQuickApp = { appName -> handleRemoteAction { onQuickApp(appName) } }
                    )

                    HomeTab.Cast -> CastScreen(
                        modifier = Modifier.padding(innerPadding),
                        isConnected = sessionState.connectedDevice != null,
                        deviceName = sessionState.connectedDevice?.name,
                        castState = sessionState.cast,
                        onOpenCastPlayer = { mediaItem ->
                            handleAction { onOpenCastPlayer(mediaItem) }
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    "Starting cast for ${mediaItem.title}",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    )

                    HomeTab.Discover, HomeTab.Settings -> {
                        Box(Modifier.fillMaxSize())
                    }
                }
            }
        }
    }

    val context = LocalContext.current
    if (sessionState.showRatingPrompt) {
        AlertDialog(
            onDismissRequest = onDismissRatingPrompt,
            title = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Enjoying the Remote?",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Text(
                    text = "If you love using this remote, please take a moment to rate us on the Play Store. Your support means the world to the developers!",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onUserRated()
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                        }
                    }
                ) {
                    Text("Rate 5 Stars", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Row {
                    TextButton(
                        onClick = {
                            onUserFeedbackClicked()
                            val feedbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:")
                                putExtra(Intent.EXTRA_EMAIL, arrayOf("harimordiya123@gmail.com"))
                                putExtra(Intent.EXTRA_SUBJECT, "Feedback on Android TV Remote App")
                                putExtra(Intent.EXTRA_TEXT, "App Version: ${BuildConfig.VERSION_NAME}\n\nFeedback:")
                            }
                            try {
                                context.startActivity(Intent.createChooser(feedbackIntent, "Send Feedback via"))
                            } catch (e: Exception) {
                                // ignore
                            }
                        }
                    ) {
                        Text("Give Feedback")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = onDismissRatingPrompt) {
                        Text("Maybe Later")
                    }
                }
            },
            shape = RoundedCornerShape(28.dp),
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    }

    if (showKeyboardDialog) {
        KeyboardDialog(
            onDismiss = { showKeyboardDialog = false },
            onInsertText = onKeyboardText,
            onBackspace = { onKeyboardBackspace(1) },
            onEnter = onKeyboardEnter
        )
    }
}

@Composable
private fun KeyboardDialog(
    onDismiss: () -> Unit,
    onInsertText: (String) -> Unit,
    onBackspace: () -> Unit,
    onEnter: () -> Unit,
) {
    var textValue by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TV Keyboard") },
        text = {
            Column {
                OutlinedTextField(
                    value = textValue,
                    onValueChange = { newValue: String ->
                        val previous = textValue
                        textValue = newValue
                        if (newValue.length > previous.length && newValue.startsWith(previous)) {
                            onInsertText(newValue.removePrefix(previous))
                        } else if (newValue.length < previous.length && previous.startsWith(newValue)) {
                            repeat(previous.length - newValue.length) { onBackspace() }
                        } else {
                            val commonPrefix = previous.commonPrefixWith(newValue).length
                            val removedCount = previous.length - commonPrefix
                            repeat(removedCount.coerceAtLeast(0)) { onBackspace() }
                            val inserted = newValue.substring(commonPrefix)
                            if (inserted.isNotEmpty()) {
                                onInsertText(inserted)
                            }
                        }
                    },
                    placeholder = { Text("Type for your TV") },
                    minLines = 3,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onEnter() })
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onEnter) {
                Text("Enter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
