package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated
import com.radonshadow.focusdrift.ui.theme.TextSecondary

/** Overlapping avatar circles used on room cards and the home screen's active-room strip. */
@Composable
fun ParticipantAvatarCluster(
    colors: List<Color>,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 28.dp,
    maxVisible: Int = 5,
    overflowCount: Int = 0
) {
    val visible = colors.take(maxVisible)
    val overflow = overflowCount + (colors.size - visible.size).coerceAtLeast(0)

    Row(modifier = modifier) {
        visible.forEachIndexed { index, color ->
            Box(
                modifier = Modifier
                    .offset(x = (-avatarSize.value * 0.35f * index).dp)
                    .size(avatarSize)
                    .background(color, CircleShape)
                    .border(2.dp, Background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    tint = Background,
                    modifier = Modifier.size(avatarSize * 0.55f)
                )
            }
        }
        if (overflow > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-avatarSize.value * 0.35f * visible.size).dp)
                    .size(avatarSize)
                    .background(SurfaceElevated, CircleShape)
                    .border(2.dp, Background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("+$overflow", color = TextSecondary, fontSize = (avatarSize.value * 0.32f).sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
