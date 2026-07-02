package com.radonshadow.focusdrift.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.core.extensions.minutesToMillis
import com.radonshadow.focusdrift.core.extensions.toHoursMinutesLabel
import com.radonshadow.focusdrift.ui.components.QuickStatCard
import com.radonshadow.focusdrift.ui.components.XpProgressBar
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.GreenSuccess
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TealRooms
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer

@Composable
fun ProfileScreen(
    onGoPro: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().background(Background).padding(horizontal = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(30.dp))
        Box(modifier = Modifier.size(88.dp).background(IndigoPrimary, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Background, modifier = Modifier.size(44.dp))
        }
        Spacer(Modifier.height(14.dp))
        Text(uiState.displayName, color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 22.sp)
        Text("Level ${uiState.userProgress.level}", color = TextSecondary, fontSize = 14.sp)

        Spacer(Modifier.height(16.dp))
        XpProgressBar(
            progress = if (uiState.userProgress.xpToNextLevel > 0) uiState.userProgress.xpIntoLevel.toFloat() / uiState.userProgress.xpToNextLevel else 0f,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(6.dp))
        Text("${uiState.userProgress.xpIntoLevel} / ${uiState.userProgress.xpToNextLevel} XP", color = TextSecondary, fontSize = 12.sp)

        if (!uiState.subscriptionStatus.isPro) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(IndigoPrimary, AmberReward)),
                        RoundedCornerShape(16.dp)
                    )
                    .clickableNoRipple(onGoPro)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.WorkspacePremium, contentDescription = null, tint = Background)
                Spacer(Modifier.width(10.dp))
                Text("Upgrade to FocusDrift Pro", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("LIFETIME STATS", color = TextSecondary, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            QuickStatCard(
                icon = Icons.Filled.LocalFireDepartment,
                label = "Longest streak",
                value = "${uiState.userProgress.longestStreak}d",
                valueColor = AmberReward,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Filled.Timer,
                label = "Total focus",
                value = uiState.userProgress.totalFocusMinutes.minutesToMillis().toHoursMinutesLabel(),
                valueColor = TealRooms,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            QuickStatCard(
                icon = Icons.Filled.WorkspacePremium,
                label = "Total sessions",
                value = "${uiState.userProgress.totalSessions}",
                valueColor = IndigoPrimary,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                icon = Icons.Filled.LocalFireDepartment,
                label = "Focus coins",
                value = "${uiState.userProgress.coins}",
                valueColor = GreenSuccess,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(Modifier.height(30.dp))
    }
}
