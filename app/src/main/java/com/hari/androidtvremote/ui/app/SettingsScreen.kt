package com.hari.androidtvremote.ui.app

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.hari.androidtvremote.BuildConfig
import com.hari.androidtvremote.ui.theme.AccentBorderBrush

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        hapticsEnabled = true,
        keepScreenAwake = true,
        autoReconnectEnabled = true,
        remoteApps = emptyList(),
        onBack = {},
        onOpenAppearance = {},
        onHapticsChange = {},
        onKeepScreenAwakeChange = {},
        onAutoReconnectChange = {},
        onRemoteAppOrderChange = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    hapticsEnabled: Boolean,
    keepScreenAwake: Boolean,
    autoReconnectEnabled: Boolean,
    remoteApps: List<RemoteShortcutApp>,
    onBack: () -> Unit,
    onOpenAppearance: () -> Unit,
    onHapticsChange: (Boolean) -> Unit,
    onKeepScreenAwakeChange: (Boolean) -> Unit,
    onAutoReconnectChange: (Boolean) -> Unit,
    onRemoteAppOrderChange: (List<String>) -> Unit,
) {
    val context = LocalContext.current
    var showAppOrderDialog by rememberSaveable { mutableStateOf(false) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(
        rememberTopAppBarState()
    )

    AppBackdrop {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                SettingsTopBar(
                    title = "Settings",
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
                    SettingsSection(title = "Interaction") {
                        SettingsSwitchItem(
                            title = "Auto Reconnect",
                            desc = "Connect to last TV automatically",
                            icon = Icons.Outlined.Autorenew,
                            checked = autoReconnectEnabled,
                            onCheckedChange = onAutoReconnectChange
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        SettingsSwitchItem(
                            title = "Haptic Feedback",
                            desc = "Vibrate on button presses",
                            icon = Icons.Filled.TouchApp,
                            checked = hapticsEnabled,
                            onCheckedChange = onHapticsChange
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        SettingsSwitchItem(
                            title = "Keep Screen Awake",
                            desc = "Prevent sleep while using the remote",
                            icon = Icons.Filled.Tv,
                            checked = keepScreenAwake,
                            onCheckedChange = onKeepScreenAwakeChange
                        )
                    }
                }

                item {
                    SettingsSection(title = "Personalization") {
                        Surface(
                            onClick = { showAppOrderDialog = true },
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.7f),
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Filled.Apps,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Quick-launch Order",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Drag and arrange app positions",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                )
                            }
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        SettingsLargeItem(
                            title = "Appearance",
                            desc = "Themes, colors, and dynamic styling",
                            icon = Icons.Outlined.Palette,
                            onClick = onOpenAppearance
                        )
                    }
                }


                item {
                    SettingsSection(title = "Support") {
                        SettingsSmallItem(
                            title = "Rate App",
                            icon = Icons.Outlined.StarRate,
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                                }
                                try {
                                    context.startActivity(intent)
                                } catch (e: ActivityNotFoundException) {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                                }
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        SettingsSmallItem(
                            title = "Share with Friends",
                            icon = Icons.Outlined.Share,
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Control your Android TV with ease: https://play.google.com/store/apps/details?id=${context.packageName}")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share App"))
                            }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
                        )
                        SettingsSmallItem(
                            title = "Feedback",
                            icon = Icons.Outlined.Feedback,
                            onClick = {
                                val feedbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:")
                                    putExtra(Intent.EXTRA_EMAIL, arrayOf("harimordiya123@gmail.com"))
                                    putExtra(Intent.EXTRA_SUBJECT, "Feedback - Remote TV")
                                    putExtra(Intent.EXTRA_TEXT, "Version: ${BuildConfig.VERSION_NAME}\n\nFeedback:")
                                }
                                try {
                                    context.startActivity(Intent.createChooser(feedbackIntent, "Send Feedback"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                    }
                }

                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp, bottom = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.4f),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Info,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Version ${BuildConfig.VERSION_NAME}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Made with ❤️ for Android TV",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

    if (showAppOrderDialog) {
        RemoteAppOrderDialog(
            apps = remoteApps,
            onDismiss = { showAppOrderDialog = false },
            onSave = { reorderedApps: List<RemoteShortcutApp> ->
                onRemoteAppOrderChange(reorderedApps.map(RemoteShortcutApp::id))
                showAppOrderDialog = false
            }
        )
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable (ColumnScope.() -> Unit)
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold
        )
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun SettingsLargeItem(
    title: String,
    desc: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SettingsSwitchItem(
    title: String,
    desc: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        onClick = { onCheckedChange(!checked) },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = desc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun SettingsSmallItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
fun RemoteAppOrderDialog(
    apps: List<RemoteShortcutApp>,
    onDismiss: () -> Unit,
    onSave: (List<RemoteShortcutApp>) -> Unit
) {
    val workingApps = remember(apps) { apps.toMutableStateList() }
    val dragState   = remember(workingApps) { DragDropState(workingApps) }
    val listState   = rememberLazyListState()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows  = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .padding(horizontal = 20.dp, vertical = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .border(
                        width = 1.dp,
                        brush = AccentBorderBrush,
                        shape = RoundedCornerShape(36.dp)
                    ),
                shape           = RoundedCornerShape(36.dp),
                color           = MaterialTheme.colorScheme.surface,
                tonalElevation  = 12.dp,
                shadowElevation = 32.dp
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // ── Header ──────────────────────────────────────
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text       = "App Shortcuts",
                                style      = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color      = MaterialTheme.colorScheme.onSurface
                            )
                            IconButton(
                                onClick  = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close",
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text  = "Arrange apps for quick access from your remote dashboard.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }

                    // ── List Area ─────────────────────────────────────────
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        LazyColumn(
                            state   = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding          = PaddingValues(bottom = 20.dp),
                            verticalArrangement     = Arrangement.spacedBy(10.dp),
                            userScrollEnabled       = !dragState.isDragging
                        ) {
                            itemsIndexed(
                                items = workingApps,
                                key   = { _, app -> app.id }
                            ) { index, app ->
                                DraggableAppItem(
                                    app       = app,
                                    index     = index,
                                    dragState = dragState
                                )
                            }
                        }

                        // Gradient fade at top and bottom
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(MaterialTheme.colorScheme.surface, Color.Transparent)
                                    )
                                )
                                .align(Alignment.TopCenter)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(20.dp)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.Transparent, MaterialTheme.colorScheme.surface)
                                    )
                                )
                                .align(Alignment.BottomCenter)
                        )
                    }

                    // ── Footer ────────────────────────────────────────────────
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            ) {
                                Text("Discard", fontWeight = FontWeight.SemiBold)
                            }
                            Button(
                                onClick = { onSave(workingApps.toList()) },
                                modifier = Modifier.weight(1.2f),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                            ) {
                                Text("Save Configuration", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DraggableAppItem(
    app: RemoteShortcutApp,
    index: Int,
    dragState: DragDropState
) {
    val density    = LocalDensity.current
    val isDragging = dragState.draggingId == app.id

    val offsetDp by animateDpAsState(
        targetValue   = if (isDragging) with(density) { dragState.dragOffsetPx.toDp() } else 0.dp,
        animationSpec = spring(stiffness = if (isDragging) 600f else 1800f),
        label         = "offset_${app.id}"
    )
    val elevation by animateDpAsState(
        targetValue   = if (isDragging) 12.dp else 2.dp,
        animationSpec = spring(stiffness = 400f),
        label         = "elevation_${app.id}"
    )
    val scale by animateFloatAsState(
        targetValue   = if (isDragging) 1.05f else 1f,
        animationSpec = spring(stiffness = 400f),
        label         = "scale_${app.id}"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                if (size.height > 0 && dragState.itemHeightPx == 0f)
                    dragState.itemHeightPx = size.height.toFloat()
            }
            .offset(y = offsetDp)
            .zIndex(if (isDragging) 2f else 0f)
            .shadow(elevation = elevation, shape = RoundedCornerShape(20.dp), clip = false)
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isDragging) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f)
            )
            .border(
                width = 1.dp,
                color = if (isDragging) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) 
                        else Color.Transparent,
                shape = RoundedCornerShape(20.dp)
            )
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .padding(14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Drag handle (moved to start) ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                .pointerInput(app.id) {
                    detectDragGesturesAfterLongPress(
                        onDragStart  = { dragState.onDragStart(app.id) },
                        onDrag       = { change, dragAmount ->
                            change.consume()
                            dragState.onDrag(dragAmount.y)
                        },
                        onDragEnd    = { dragState.onDragEnd() },
                        onDragCancel = { dragState.onDragEnd() }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Filled.DragHandle,
                contentDescription = "Drag to reorder",
                modifier           = Modifier.size(20.dp),
                tint = if (isDragging) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }

        // ── App Icon ─────────────────────────────────────────────────────────
        Surface(
            modifier = Modifier.size(44.dp),
            shape = RoundedCornerShape(12.dp),
            color = if (app.iconUrl == null) app.accent.copy(alpha = 0.2f) else Color.Transparent,
            tonalElevation = 2.dp
        ) {
            if (app.iconUrl != null) {
                AsyncImage(
                    model = app.iconUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(6.dp)
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = app.mark,
                        style = MaterialTheme.typography.titleMedium,
                        color = app.accent,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ── App label ─────────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = app.label,
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = if (isDragging) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurface
            )
            if (index == 0 && !dragState.isDragging) {
                Text(
                    text = "Primary Shortcut",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // ── Position badge ────────────────────────────────────────────────────
        Surface(
            shape    = CircleShape,
            color    = if (isDragging) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.size(30.dp)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text       = "${index + 1}",
                    style      = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color      = if (isDragging) MaterialTheme.colorScheme.onPrimary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private class DragDropState(
    val items: SnapshotStateList<RemoteShortcutApp>
) {
    var draggingId    by mutableStateOf<String?>(null)
    var dragOffsetPx  by mutableFloatStateOf(0f)
    var itemHeightPx  by mutableFloatStateOf(0f)

    val isDragging get() = draggingId != null

    fun onDragStart(id: String) {
        draggingId   = id
        dragOffsetPx = 0f
    }

    fun onDrag(deltaY: Float) {
        if (draggingId == null) return
        dragOffsetPx += deltaY

        val step = itemHeightPx.takeIf { it > 0f } ?: return
        var idx  = items.indexOfFirst { it.id == draggingId } .takeIf { it >= 0 } ?: return

        while (dragOffsetPx > step / 2f && idx < items.lastIndex) {
            val moved = items.removeAt(idx)
            items.add(idx + 1, moved)
            dragOffsetPx -= step
            idx++
        }
        while (dragOffsetPx < -step / 2f && idx > 0) {
            val moved = items.removeAt(idx)
            items.add(idx - 1, moved)
            dragOffsetPx += step
            idx--
        }
    }

    fun onDragEnd() {
        draggingId   = null
        dragOffsetPx = 0f
    }
}
