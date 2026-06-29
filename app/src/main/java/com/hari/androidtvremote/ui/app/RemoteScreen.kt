package com.hari.androidtvremote.ui.app

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.KeyboardBackspace
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.hari.androidtvremote.androidLib.remote.Remotemessage
import com.hari.androidtvremote.ui.theme.AccentBlue
import com.hari.androidtvremote.ui.theme.AccentBorderBrush
import com.hari.androidtvremote.ui.theme.AccentBorderSoftBrush
import com.hari.androidtvremote.ui.theme.AccentCoral
import com.hari.androidtvremote.ui.theme.AccentMagenta
import com.hari.androidtvremote.ui.theme.AccentPinkRed
import com.hari.androidtvremote.ui.theme.AccentRingBrush
import com.hari.androidtvremote.ui.theme.OkButtonBrush
import androidx.compose.ui.graphics.StrokeCap
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
private fun rememberPressScale(
    interactionSource: MutableInteractionSource,
    pressedScale: Float = 0.92f,
): Float {
    val pressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (pressed) pressedScale else 1f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 620f),
        label = "buttonPressScale"
    )
    return scale
}

@Composable
fun RemoteScreen(
    modifier: Modifier = Modifier,
    activePadMode: RemotePadMode,
    defaultPadMode: RemotePadMode,
    sessionState: TvRemoteUiState,
    onRemoteKey: (Remotemessage.RemoteKeyCode) -> Unit,
    onVolumeUp: () -> Unit,
    onVolumeDown: () -> Unit,
    onToggleVoice: () -> Unit,
) {
    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasMicPermission = granted
        if (granted) onToggleVoice()
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val horizontalPadding = when {
            maxWidth < 360.dp -> 10.dp
            maxWidth < 420.dp -> 14.dp
            else -> 20.dp
        }
        val verticalGap = when {
            maxHeight < 620.dp -> 8.dp
            maxHeight < 760.dp -> 12.dp
            else -> 16.dp
        }
        val actionIconSize = when {
            maxWidth < 360.dp -> 22.dp
            maxWidth < 420.dp -> 24.dp
            else -> 28.dp
        }
        val controlSpacing = when {
            maxWidth < 360.dp -> 8.dp
            maxWidth < 420.dp -> 10.dp
            else -> 12.dp
        }
        val rockerWidth = when {
            maxWidth < 360.dp -> 56.dp
            maxWidth < 420.dp -> 62.dp
            else -> 68.dp
        }
        val rockerHeight = when {
            maxHeight < 620.dp -> 156.dp
            maxHeight < 760.dp -> 170.dp
            else -> 184.dp
        }
        val padStageHeight = minOf(
            maxWidth - (horizontalPadding * 2),
            maxHeight * 0.44f,
            352.dp
        ).coerceAtLeast(300.dp)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(verticalGap)
        ) {
            RemotePadStage(
                activePadMode = activePadMode,
                stageHeight = padStageHeight,
                onAction = onRemoteKey
            )
            Spacer(modifier = Modifier.height(4.dp))

            RemoteControlDeck(
                isVoiceActive = sessionState.isVoiceActive,
                rockerWidth = rockerWidth,
                rockerHeight = rockerHeight,
                controlSpacing = controlSpacing,
                actionIconSize = actionIconSize,
                onVoice = {
                    if (hasMicPermission) {
                        onToggleVoice()
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                },
                onBack = { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_BACK) },
                onVolumeDown = onVolumeDown,
                onVolumeUp = onVolumeUp,
                onChannelUp = { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_CHANNEL_UP) },
                onChannelDown = { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_CHANNEL_DOWN) },
                onRewind = { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_MEDIA_REWIND) },
                onPlayPause = { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_MEDIA_PLAY_PAUSE) },
                onFastForward = { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_MEDIA_FAST_FORWARD) },
                onStop = { onRemoteKey(Remotemessage.RemoteKeyCode.KEYCODE_MEDIA_STOP) }
            )
        }
    }
}

@Composable
private fun RemotePadStage(
    activePadMode: RemotePadMode,
    stageHeight: Dp,
    onAction: (Remotemessage.RemoteKeyCode) -> Unit,
) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(stageHeight),
        contentAlignment = Alignment.Center
    ) {
        val basePadSize = minOf(maxWidth, stageHeight)
        val widePadWidth = minOf(maxWidth, (basePadSize * 1.08f).coerceAtLeast(basePadSize))

        AnimatedContent(targetState = activePadMode, label = "remotePad") { mode ->
            when (mode) {
                RemotePadMode.DPad -> GoogleTvDPad(
                    size = basePadSize,
                    onAction = onAction
                )

                RemotePadMode.Touchpad -> TouchpadPanel(
                    width = widePadWidth,
                    height = basePadSize,
                    onAction = onAction
                )

                RemotePadMode.NumberPad -> NumberPadPanel(
                    width = widePadWidth,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun RemoteControlDeck(
    isVoiceActive: Boolean,
    rockerWidth: Dp,
    rockerHeight: Dp,
    controlSpacing: Dp,
    actionIconSize: Dp,
    onVoice: () -> Unit,
    onBack: () -> Unit,
    onVolumeDown: () -> Unit,
    onVolumeUp: () -> Unit,
    onChannelUp: () -> Unit,
    onChannelDown: () -> Unit,
    onRewind: () -> Unit,
    onPlayPause: () -> Unit,
    onFastForward: () -> Unit,
    onStop: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(controlSpacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RemoteVerticalRocker(
            modifier = Modifier.width(rockerWidth),
            label = "VOL",
            topIcon = Icons.Filled.Add,
            bottomIcon = Icons.Filled.Remove,
            height = rockerHeight,
            iconSize = actionIconSize,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            onTopClick = onVolumeUp,
            onBottomClick = onVolumeDown
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(controlSpacing)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(controlSpacing)
            ) {
                RemoteActionBubble(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.FastRewind,
                    contentDescription = "Rewind",
                    onClick = onRewind,
                    iconSize = actionIconSize
                )
                RemoteActionBubble(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    onClick = onPlayPause,
                    iconSize = actionIconSize
                )
                RemoteActionBubble(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.FastForward,
                    contentDescription = "Fast Forward",
                    onClick = onFastForward,
                    iconSize = actionIconSize
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(controlSpacing)
            ) {
                RemoteActionBubble(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Stop,
                    contentDescription = "Stop",
                    onClick = onStop,
                    iconSize = actionIconSize
                )
                RemoteActionBubble(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.Mic,
                    contentDescription = if (isVoiceActive) "Stop voice search" else "Voice search",
                    onClick = onVoice,
                    emphasized = isVoiceActive,
                    iconSize = actionIconSize
                )
                RemoteActionBubble(
                    modifier = Modifier.weight(1f),
                    icon = Icons.AutoMirrored.Filled.KeyboardBackspace,
                    contentDescription = "Back",
                    onClick = onBack,
                    iconSize = actionIconSize
                )
            }
        }
        RemoteVerticalRocker(
            modifier = Modifier.width(rockerWidth),
            label = "CH",
            topIcon = Icons.Filled.KeyboardArrowUp,
            bottomIcon = Icons.Filled.KeyboardArrowDown,
            height = rockerHeight,
            iconSize = actionIconSize,
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
            onTopClick = onChannelUp,
            onBottomClick = onChannelDown
        )
    }
}

@Composable
private fun RemoteVerticalRocker(
    modifier: Modifier = Modifier,
    label: String,
    topIcon: ImageVector,
    bottomIcon: ImageVector,
    height: Dp,
    iconSize: Dp,
    contentColor: Color,
    onTopClick: () -> Unit,
    onBottomClick: () -> Unit,
) {
    val rockerShape = RoundedCornerShape(30.dp)
    Box(
        modifier = modifier
            .height(height)
            .clip(rockerShape)
            .background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
            .border(width = 1.5.dp, brush = AccentBorderBrush, shape = rockerShape)
    ) {
        val topInteraction = remember { MutableInteractionSource() }
        val bottomInteraction = remember { MutableInteractionSource() }
        val topScale = rememberPressScale(topInteraction, pressedScale = 0.90f)
        val bottomScale = rememberPressScale(bottomInteraction, pressedScale = 0.90f)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(
                        interactionSource = topInteraction,
                        indication = androidx.compose.foundation.LocalIndication.current,
                        onClick = onTopClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = topIcon,
                    contentDescription = "$label up",
                    modifier = Modifier
                        .size(iconSize)
                        .scale(topScale),
                    tint = contentColor
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
                letterSpacing = 1.sp,
                color = contentColor
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clickable(
                        interactionSource = bottomInteraction,
                        indication = androidx.compose.foundation.LocalIndication.current,
                        onClick = onBottomClick
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = bottomIcon,
                    contentDescription = "$label down",
                    modifier = Modifier
                        .size(iconSize)
                        .scale(bottomScale),
                    tint = contentColor
                )
            }
        }
    }
}

@Composable
private fun RemoteActionBubble(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    emphasized: Boolean = false,
    iconSize: Dp,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressScale = rememberPressScale(interactionSource, pressedScale = 0.88f)
    FilledTonalIconButton(
        modifier = modifier
            .aspectRatio(1f)
            .scale(pressScale)
            .border(
                width = 1.5.dp,
                brush = if (emphasized) AccentBorderBrush else AccentBorderSoftBrush,
                shape = CircleShape
            ),
        onClick = onClick,
        shape = CircleShape,
        interactionSource = interactionSource,
        colors = IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = if (emphasized) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerHigh
            }
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
            tint = if (emphasized) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

@Composable
private fun GoogleTvDPad(
    size: Dp,
    onAction: (Remotemessage.RemoteKeyCode) -> Unit,
) {
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    var activeZone by remember { mutableStateOf<DPadZone?>(null) }
    val padScale by animateFloatAsState(
        targetValue = if (activeZone != null) 0.985f else 1f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 520f),
        label = "dpadScale"
    )
    val edgeOffset = if (size < 260.dp) 20.dp else 28.dp
    val sideOffset = if (size < 260.dp) 18.dp else 26.dp
    val edgeBubbleSize = if (size < 260.dp) 30.dp else 34.dp

    fun activate(zone: DPadZone, key: Remotemessage.RemoteKeyCode) {
        activeZone = zone
        haptics.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
        onAction(key)
        scope.launch {
            delay(160)
            if (activeZone == zone) {
                activeZone = null
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .scale(padScale),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .matchParentSize()
                    .border(width = 2.dp, brush = AccentRingBrush, shape = CircleShape),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shadowElevation = 16.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.16f),
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                    MaterialTheme.colorScheme.surfaceContainer
                                )
                            )
                        )
                ) {
                    Canvas(modifier = Modifier.matchParentSize()) {
                        val outerRadius = this.size.minDimension / 2f
                        val innerRadius = outerRadius * 0.41f
                        val separators = listOf(
                            45f to AccentMagenta,
                            135f to AccentCoral,
                            225f to AccentPinkRed,
                            315f to AccentBlue,
                        )
                        separators.forEach { (angle, color) ->
                            val radians = Math.toRadians(angle.toDouble()).toFloat()
                            val start = Offset(
                                x = center.x + cos(radians) * innerRadius,
                                y = center.y + sin(radians) * innerRadius
                            )
                            val end = Offset(
                                x = center.x + cos(radians) * outerRadius,
                                y = center.y + sin(radians) * outerRadius
                            )
                            drawLine(
                                color = color,
                                start = start,
                                end = end,
                                strokeWidth = 0.5f,
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    DirectionalZone(
                        modifier = Modifier.matchParentSize(),
                        zone = DPadZone.Up,
                        activeZone = activeZone,
                        icon = {
                            Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Up")
                        },
                        iconAlignment = Alignment.TopCenter,
                        iconOffsetY = edgeOffset,
                        bubbleSize = edgeBubbleSize,
                        onClick = {
                            activate(
                                DPadZone.Up,
                                Remotemessage.RemoteKeyCode.KEYCODE_DPAD_UP
                            )
                        }
                    )
                    DirectionalZone(
                        modifier = Modifier.matchParentSize(),
                        zone = DPadZone.Right,
                        activeZone = activeZone,
                        icon = {
                            Icon(
                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Right"
                            )
                        },
                        iconAlignment = Alignment.CenterEnd,
                        iconOffsetX = sideOffset * -1f,
                        bubbleSize = edgeBubbleSize,
                        onClick = {
                            activate(
                                DPadZone.Right,
                                Remotemessage.RemoteKeyCode.KEYCODE_DPAD_RIGHT
                            )
                        }
                    )
                    DirectionalZone(
                        modifier = Modifier.matchParentSize(),
                        zone = DPadZone.Down,
                        activeZone = activeZone,
                        icon = {
                            Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Down")
                        },
                        iconAlignment = Alignment.BottomCenter,
                        iconOffsetY = edgeOffset * -1f,
                        bubbleSize = edgeBubbleSize,
                        onClick = {
                            activate(
                                DPadZone.Down,
                                Remotemessage.RemoteKeyCode.KEYCODE_DPAD_DOWN
                            )
                        }
                    )
                    DirectionalZone(
                        modifier = Modifier.matchParentSize(),
                        zone = DPadZone.Left,
                        activeZone = activeZone,
                        icon = {
                            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Left")
                        },
                        iconAlignment = Alignment.CenterStart,
                        iconOffsetX = sideOffset,
                        bubbleSize = edgeBubbleSize,
                        onClick = {
                            activate(
                                DPadZone.Left,
                                Remotemessage.RemoteKeyCode.KEYCODE_DPAD_LEFT
                            )
                        }
                    )
                }
            }

            val centerPressed = activeZone == DPadZone.Center
            val centerScale by animateFloatAsState(
                targetValue = if (centerPressed) 0.96f else 1f,
                animationSpec = spring(dampingRatio = 0.72f, stiffness = 560f),
                label = "centerScale"
            )
            Surface(
                modifier = Modifier
                    .size(size * 0.41f)
                    .scale(centerScale)
                    .clip(CircleShape)
                    .clickable {
                        activate(DPadZone.Center, Remotemessage.RemoteKeyCode.KEYCODE_DPAD_CENTER)
                    },
                shape = CircleShape,
                color = Color.Transparent,
                shadowElevation = 18.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(OkButtonBrush)
                        .border(width = 1.5.dp, brush = AccentBorderBrush, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "OK",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun DirectionalZone(
    modifier: Modifier = Modifier,
    zone: DPadZone,
    activeZone: DPadZone?,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    iconAlignment: Alignment,
    iconOffsetX: Dp = 0.dp,
    iconOffsetY: Dp = 0.dp,
    bubbleSize: Dp = 34.dp,
) {
    val isActive = activeZone == zone
    val overlayAlpha by animateFloatAsState(
        targetValue = if (isActive) 1f else 0f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 520f),
        label = "${zone.name}Overlay"
    )
    val zoneScale by animateFloatAsState(
        targetValue = if (isActive) 1.02f else 1f,
        animationSpec = spring(dampingRatio = 0.78f, stiffness = 520f),
        label = "${zone.name}Scale"
    )

    Box(
        modifier = modifier
            .scale(zoneScale)
            .clip(zone.shape)
            .background(
                Brush.radialGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f * overlayAlpha),
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f * overlayAlpha),
                        Color.Transparent
                    )
                )
            )
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.04f * overlayAlpha),
                    zone.shape
                )
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = iconOffsetX, y = iconOffsetY),
            contentAlignment = iconAlignment
        ) {
            Box(
                modifier = Modifier
                    .size(bubbleSize)
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.42f + (0.14f * overlayAlpha)),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        }
    }
}

private enum class DPadZone(val startAngle: Float) {
    Up(225f),
    Right(315f),
    Down(45f),
    Left(135f),
    Center(0f);

    val shape
        get() = directionalSectorShape(startAngle)
}

private fun directionalSectorShape(
    startAngle: Float,
    sweepAngle: Float = 90f,
    innerRadiusFraction: Float = 0.40f,
) = GenericShape { size, _ ->
    val path = Path()
    val outerRadius = size.minDimension / 2f
    val innerRadius = outerRadius * innerRadiusFraction
    val center = Offset(size.width / 2f, size.height / 2f)
    val outerRect = Rect(Offset.Zero, size)
    val innerRect = Rect(
        left = center.x - innerRadius,
        top = center.y - innerRadius,
        right = center.x + innerRadius,
        bottom = center.y + innerRadius
    )

    fun pointOnCircle(radius: Float, angleDegrees: Float): Offset {
        val radians = Math.toRadians(angleDegrees.toDouble()).toFloat()
        return Offset(
            x = center.x + cos(radians) * radius,
            y = center.y + sin(radians) * radius
        )
    }

    val startPoint = pointOnCircle(outerRadius, startAngle)
    path.moveTo(startPoint.x, startPoint.y)
    path.arcTo(outerRect, startAngle, sweepAngle, false)
    path.arcTo(innerRect, startAngle + sweepAngle, -sweepAngle, false)
    path.close()
    addPath(path)
}

@Composable
private fun TouchpadPanel(
    width: Dp,
    height: Dp,
    onAction: (Remotemessage.RemoteKeyCode) -> Unit,
) {
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var isTouchActive by remember { mutableStateOf(false) }
    val dotScale by animateFloatAsState(
        targetValue = if (isTouchActive) 1.15f else 1f,
        label = "touchpadDot"
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(34.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.24f),
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.82f),
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.18f)
                    )
                )
            )
            .border(
                width = 1.8.dp,
                brush = AccentBorderBrush,
                shape = RoundedCornerShape(34.dp)
            )
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isTouchActive = true
                        tryAwaitRelease()
                        dragOffset = Offset.Zero
                        isTouchActive = false
                    },
                    onTap = {
                        onAction(Remotemessage.RemoteKeyCode.KEYCODE_DPAD_CENTER)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragOffset = Offset.Zero
                        isTouchActive = true
                    },
                    onDragCancel = {
                        dragOffset = Offset.Zero
                        isTouchActive = false
                    },
                    onDragEnd = {
                        val horizontal = abs(dragOffset.x)
                        val vertical = abs(dragOffset.y)
                        val threshold = 54f
                        when {
                            horizontal < threshold && vertical < threshold ->
                                onAction(Remotemessage.RemoteKeyCode.KEYCODE_DPAD_CENTER)

                            horizontal > vertical && dragOffset.x > 0f ->
                                onAction(Remotemessage.RemoteKeyCode.KEYCODE_DPAD_RIGHT)

                            horizontal > vertical && dragOffset.x < 0f ->
                                onAction(Remotemessage.RemoteKeyCode.KEYCODE_DPAD_LEFT)

                            dragOffset.y > 0f ->
                                onAction(Remotemessage.RemoteKeyCode.KEYCODE_DPAD_DOWN)

                            else ->
                                onAction(Remotemessage.RemoteKeyCode.KEYCODE_DPAD_UP)
                        }

                        dragOffset = Offset.Zero
                        isTouchActive = false
                    }
                ) { change, dragAmount ->
                    change.consume()
                    dragOffset += dragAmount
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(10.dp)
                .border(
                    width = 1.dp,
                    brush = AccentBorderSoftBrush,
                    shape = RoundedCornerShape(28.dp)
                )
        )
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (dragOffset.x / 5f).roundToInt(),
                        y = (dragOffset.y / 5f).roundToInt()
                    )
                }
                .scale(dotScale)
                .size(if (isTouchActive) 16.dp else 10.dp)
                .zIndex(0f)
                .background(
                    color = if (isTouchActive) {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.82f)
                    } else {
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.30f)
                    },
                    shape = CircleShape
                )
        )
    }
}

@Composable
private fun NumberPadPanel(
    width: Dp,
    onAction: (Remotemessage.RemoteKeyCode) -> Unit,
) {
    val keys = remember {
        listOf(
            "1" to Remotemessage.RemoteKeyCode.KEYCODE_1,
            "2" to Remotemessage.RemoteKeyCode.KEYCODE_2,
            "3" to Remotemessage.RemoteKeyCode.KEYCODE_3,
            "4" to Remotemessage.RemoteKeyCode.KEYCODE_4,
            "5" to Remotemessage.RemoteKeyCode.KEYCODE_5,
            "6" to Remotemessage.RemoteKeyCode.KEYCODE_6,
            "7" to Remotemessage.RemoteKeyCode.KEYCODE_7,
            "8" to Remotemessage.RemoteKeyCode.KEYCODE_8,
            "9" to Remotemessage.RemoteKeyCode.KEYCODE_9,
            "*" to Remotemessage.RemoteKeyCode.KEYCODE_STAR,
            "0" to Remotemessage.RemoteKeyCode.KEYCODE_0,
            "#" to Remotemessage.RemoteKeyCode.KEYCODE_POUND
        )
    }

    val buttonPadding = if (width < 260.dp) 12.dp else 16.dp
    val gridSpacing = if (width < 260.dp) 8.dp else 12.dp

    val padShape = RoundedCornerShape(28.dp)
    Column(
        modifier = Modifier
            .width(width)
            .clip(padShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.18f),
                        MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.82f),
                        MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.16f)
                    )
                )
            )
            .border(width = 1.5.dp, brush = AccentBorderBrush, shape = padShape)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(gridSpacing)
    ) {
        keys.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(gridSpacing)
            ) {
                row.forEach { (label, keyCode) ->
                    val keyShape = RoundedCornerShape(18.dp)
                    val keyInteraction = remember { MutableInteractionSource() }
                    val keyScale = rememberPressScale(keyInteraction, pressedScale = 0.90f)
                    FilledTonalButton(
                        onClick = { onAction(keyCode) },
                        modifier = Modifier
                            .weight(1f)
                            .scale(keyScale)
                            .border(width = 1.2.dp, brush = AccentBorderSoftBrush, shape = keyShape),
                        shape = keyShape,
                        interactionSource = keyInteraction,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        ),
                        contentPadding = PaddingValues(vertical = buttonPadding)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }
            }
        }
    }
}
