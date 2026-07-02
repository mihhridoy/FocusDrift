package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.domain.model.BodyDoubleRoom
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TealRooms
import com.radonshadow.focusdrift.ui.theme.TextSecondary

private val AVATAR_COLORS = listOf(
    com.radonshadow.focusdrift.ui.theme.IndigoPrimary,
    com.radonshadow.focusdrift.ui.theme.AmberReward,
    com.radonshadow.focusdrift.ui.theme.GreenSuccess,
    TealRooms
)

@Composable
fun BodyDoubleRoomCard(
    room: BodyDoubleRoom,
    onJoin: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = TealRooms
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(96.dp)
                .background(accentColor, RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .background(Surface, RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 4.dp, topStart = 4.dp))
                .padding(16.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(room.name, color = com.radonshadow.focusdrift.ui.theme.TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Box(
                    modifier = Modifier
                        .background(TealRooms, RoundedCornerShape(99.dp))
                        .clickableNoRipple(onJoin)
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                ) {
                    Text("Join", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(12.dp))
            ParticipantAvatarCluster(
                colors = AVATAR_COLORS.take(room.participantCount.coerceAtMost(4)),
                overflowCount = (room.participantCount - 4).coerceAtLeast(0)
            )
            Spacer(Modifier.height(8.dp))

            val statusText = buildString {
                append("${room.focusingCount} focusing")
                if (room.onBreakCount > 0) append(" · ${room.onBreakCount} on break")
            }
            Text(statusText, color = TextSecondary, fontSize = 12.sp)
        }
    }
}
