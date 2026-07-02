package com.radonshadow.focusdrift.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.radonshadow.focusdrift.ui.theme.IndigoLight
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated

@Composable
fun XpProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    animate: Boolean = true
) {
    val target = progress.coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = target,
        animationSpec = tween(if (animate) 1400 else 0),
        label = "xpProgress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(SurfaceElevated, RoundedCornerShape(99.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .align(Alignment.CenterStart)
                .background(
                    Brush.horizontalGradient(listOf(IndigoPrimary, IndigoLight)),
                    RoundedCornerShape(99.dp)
                )
        )
    }
}
