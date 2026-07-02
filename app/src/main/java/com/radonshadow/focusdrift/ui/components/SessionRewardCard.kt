package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun SessionRewardCard(
    xpEarned: Int,
    xpBefore: Int,
    xpAfter: Int,
    xpProgress: Float,
    coinsEarned: Int,
    currentStreak: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(22.dp))
            .border(1.dp, Border, RoundedCornerShape(22.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("+$xpEarned XP", color = IndigoPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text("$xpBefore → $xpAfter", color = TextSecondary, fontSize = 12.sp)
        }

        Spacer(Modifier.height(12.dp))
        XpProgressBar(progress = xpProgress)
        Spacer(Modifier.height(16.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            RewardChip(modifier = Modifier.weight(1f), icon = Icons.Filled.MonetizationOn, label = "+$coinsEarned Coin${if (coinsEarned == 1) "" else "s"}")
            RewardChip(modifier = Modifier.weight(1f), icon = Icons.Filled.LocalFireDepartment, label = "$currentStreak days")
        }
    }
}

@Composable
private fun RewardChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(SurfaceElevated, RoundedCornerShape(14.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = AmberReward)
        Spacer(Modifier.height(4.dp))
        Text(label, color = AmberReward, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}
