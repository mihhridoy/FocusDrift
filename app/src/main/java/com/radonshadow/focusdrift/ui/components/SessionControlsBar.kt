package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.GreenSuccess
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TextPrimary

@Composable
fun SessionControlsBar(
    isPaused: Boolean,
    onPauseResumeClick: () -> Unit,
    onResetClick: () -> Unit,
    onCompleteEarlyClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(26.dp)) {
        ControlButton(icon = if (isPaused) Icons.Filled.PlayArrow else Icons.Filled.Pause, tint = TextPrimary, onClick = onPauseResumeClick)
        ControlButton(icon = Icons.Filled.Refresh, tint = TextPrimary, onClick = onResetClick)
        ControlButton(icon = Icons.Filled.Check, tint = GreenSuccess, onClick = onCompleteEarlyClick)
    }
}

@Composable
private fun ControlButton(icon: androidx.compose.ui.graphics.vector.ImageVector, tint: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(Surface, CircleShape)
            .border(1.dp, Border, CircleShape)
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint)
    }
}
