package com.radonshadow.focusdrift.core.extensions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawOutline
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * clickable() without the default ripple/indication — used for large card taps where the
 * design has its own pressed-state treatment instead of a Material ripple.
 */
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        indication = null,
        interactionSource = MutableInteractionSource(),
        onClick = onClick
    )
)

/** Dashed outline used for "create new" placeholder cards — Compose has no built-in dashed border. */
fun Modifier.dashedBorder(color: Color, shape: Shape, width: Dp = 1.5.dp): Modifier = this.then(
    Modifier.drawBehind {
        val outline = shape.createOutline(size, layoutDirection, this)
        drawOutline(
            outline = outline,
            color = color,
            style = Stroke(
                width = width.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            )
        )
    }
)
