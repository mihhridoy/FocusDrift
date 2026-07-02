package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Nunito

/**
 * ADHD users drift. This button acknowledges it without shame instead of framing it as a failure
 * — it pauses the session and offers a judgment-free way back in.
 */
@Composable
fun DriftButton(
    isDrifting: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isDrifting) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp)
                .background(AmberReward, RoundedCornerShape(99.dp))
                .clickableNoRipple(onClick),
            contentAlignment = Alignment.Center
        ) {
            Text("It's okay — resume", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
        }
    } else {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(52.dp)
                .border(1.5.dp, AmberReward, RoundedCornerShape(99.dp))
                .clickableNoRipple(onClick),
            contentAlignment = Alignment.Center
        ) {
            Text("I'm drifting", color = AmberReward, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }
    }
}
