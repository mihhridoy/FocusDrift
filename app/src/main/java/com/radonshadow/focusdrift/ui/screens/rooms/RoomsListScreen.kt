package com.radonshadow.focusdrift.ui.screens.rooms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.radonshadow.focusdrift.core.extensions.dashedBorder
import com.radonshadow.focusdrift.ui.components.BodyDoubleRoomCard
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.TealRooms
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun RoomsListScreen(
    onCreateRoom: () -> Unit,
    onOpenRoom: (String) -> Unit,
    viewModel: RoomsViewModel = hiltViewModel()
) {
    val rooms by viewModel.activeRooms.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Body Double Rooms", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Text("Create +", color = TealRooms, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.clickableNoRipple(onCreateRoom))
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 22.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(rooms, key = { it.id }) { room ->
                BodyDoubleRoomCard(room = room, onJoin = { onOpenRoom(room.id) })
            }
            item {
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .dashedBorder(Border, RoundedCornerShape(20.dp))
                        .padding(18.dp)
                        .clickableNoRipple(onCreateRoom),
                    contentAlignment = Alignment.Center
                ) {
                    Text("+ Create your own room", color = TextSecondary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
