package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated
import com.radonshadow.focusdrift.ui.theme.TextSecondary

private val DAY_LABELS = listOf("M", "T", "W", "T", "F", "S", "S")

/** Mon–Sun completion strip used on habit cards; [completion] must have exactly 7 entries. */
@Composable
fun WeekDotRow(
    completion: List<Boolean>,
    modifier: Modifier = Modifier,
    filledColor: Color = com.radonshadow.focusdrift.ui.theme.GreenSuccess
) {
    require(completion.size == 7) { "WeekDotRow expects 7 days, got ${completion.size}" }

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        completion.forEachIndexed { index, done ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(22.dp)
                    .background(if (done) filledColor else SurfaceElevated, RoundedCornerShape(7.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = DAY_LABELS[index],
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (done) Background else TextSecondary
                )
            }
        }
    }
}
