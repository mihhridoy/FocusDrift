package com.radonshadow.focusdrift.ui.screens.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.domain.model.Participant
import com.radonshadow.focusdrift.domain.model.ParticipantStatus
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.GreenSuccess
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TealRooms
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun RoomDetailScreen(
    roomId: String,
    onBack: () -> Unit,
    viewModel: RoomsViewModel = hiltViewModel()
) {
    val room by viewModel.currentRoom.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(roomId) { viewModel.observeRoom(roomId) }
    DisposableEffect(roomId) { onDispose { viewModel.leaveRoom(roomId) } }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).height(54.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickableNoRipple(onBack)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                Text(" ${room?.name ?: ""}", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }

        Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), contentAlignment = Alignment.Center) {
            Row(
                modifier = Modifier
                    .background(Surface, RoundedCornerShape(99.dp))
                    .border(1.dp, Border, RoundedCornerShape(99.dp))
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).background(GreenSuccess, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text("Focus phase", color = TealRooms, fontFamily = com.radonshadow.focusdrift.ui.theme.DmMono, fontSize = 15.sp)
            }
        }

        val participants = room?.participants.orEmpty()
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(24.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(participants, key = { it.userId }) { participant ->
                ParticipantTile(participant)
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
            Text(
                "Chat is off during focus — send a reaction",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            if (errorMessage != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    errorMessage.orEmpty(),
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            Spacer(Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                ReactionButton(Icons.Filled.ThumbUp) { viewModel.sendReaction(roomId, "thumbs_up") }
                Spacer(Modifier.width(12.dp))
                ReactionButton(Icons.Filled.LocalFireDepartment) { viewModel.sendReaction(roomId, "fire") }
                Spacer(Modifier.width(12.dp))
                ReactionButton(Icons.Filled.FitnessCenter) { viewModel.sendReaction(roomId, "strength") }
            }
        }
    }
}

@Composable
private fun ReactionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(Surface, CircleShape)
            .border(1.dp, Border, CircleShape)
            .clickableNoRipple(onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = TextPrimary)
    }
}

@Composable
private fun ParticipantTile(participant: Participant) {
    val ringColor = when (participant.status) {
        ParticipantStatus.FOCUSING -> TealRooms
        ParticipantStatus.ON_BREAK -> AmberReward
        ParticipantStatus.IDLE -> Border
    }
    val avatarColor = runCatching { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(participant.avatarColor)) }
        .getOrDefault(TealRooms)

    Column(
        modifier = Modifier
            .background(Surface, RoundedCornerShape(18.dp))
            .border(2.dp, ringColor, RoundedCornerShape(18.dp))
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.size(52.dp).background(avatarColor, CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Filled.Person, contentDescription = null, tint = Background)
        }
        Spacer(Modifier.height(8.dp))
        Text(participant.displayName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(
            if (participant.status == ParticipantStatus.ON_BREAK) "On break" else participant.currentTask.ifBlank { "Focusing" },
            color = if (participant.status == ParticipantStatus.ON_BREAK) AmberReward else TextSecondary,
            fontSize = 11.sp
        )
    }
}
