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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.constants.TimerConstants
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.core.extensions.minutesToMillis
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

private val ROOM_TYPES = listOf("Deep Work", "Study Hall", "Creative Sprint", "Night Owls", "Freelancer Focus")

@Composable
fun CreateRoomScreen(
    onCreated: (String) -> Unit,
    viewModel: RoomsViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ROOM_TYPES.first()) }
    var minutes by remember { mutableStateOf(TimerConstants.DEFAULT_FOCUS_MINUTES) }
    val isBusy by viewModel.isBusy.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background).padding(24.dp)) {
        Text("Create a room", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Room name", color = TextSecondary) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = IndigoPrimary,
                unfocusedBorderColor = Border
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(20.dp))
        Text("Room type", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ROOM_TYPES.forEach { type ->
                val selected = type == selectedType
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (selected) IndigoPrimary else Surface, RoundedCornerShape(14.dp))
                        .border(1.dp, if (selected) IndigoPrimary else Border, RoundedCornerShape(14.dp))
                        .clickableNoRipple { selectedType = type }
                        .padding(14.dp)
                ) {
                    Text(type, color = if (selected) Background else TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Session length: $minutes min", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        androidx.compose.material3.Slider(
            value = minutes.toFloat(),
            onValueChange = { minutes = it.toInt() },
            valueRange = TimerConstants.MIN_FOCUS_MINUTES.toFloat()..TimerConstants.MAX_FOCUS_MINUTES.toFloat(),
            colors = androidx.compose.material3.SliderDefaults.colors(
                thumbColor = TextPrimary,
                activeTrackColor = IndigoPrimary,
                inactiveTrackColor = Border
            )
        )

        if (errorMessage != null) {
            Spacer(Modifier.height(12.dp))
            Text(errorMessage.orEmpty(), color = androidx.compose.ui.graphics.Color.Red, fontSize = 13.sp)
        }

        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(if (name.isNotBlank() && !isBusy) IndigoPrimary else Border, RoundedCornerShape(99.dp))
                .clickableNoRipple {
                    if (name.isNotBlank() && !isBusy) {
                        viewModel.createRoom(name, selectedType, minutes.minutesToMillis(), onCreated)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(if (isBusy) "Creating…" else "Create room", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        }
    }
}
