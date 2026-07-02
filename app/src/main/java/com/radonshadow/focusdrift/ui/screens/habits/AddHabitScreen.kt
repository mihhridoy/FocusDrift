package com.radonshadow.focusdrift.ui.screens.habits

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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.domain.model.HabitFrequency
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

private val EMOJI_OPTIONS = listOf("💧", "📖", "🧘", "🏃", "🧹", "💊", "📝", "🎨", "🎵", "🌱", "🦷", "☀️")
private val TINT_OPTIONS = listOf("#7C6FE0", "#4ECDC4", "#F4A732", "#6BCB77", "#FF6B6B")

@Composable
fun AddHabitScreen(
    onDone: () -> Unit,
    viewModel: HabitsViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var emoji by remember { mutableStateOf(EMOJI_OPTIONS.first()) }
    var frequency by remember { mutableStateOf(HabitFrequency.DAILY) }
    var tint by remember { mutableStateOf(TINT_OPTIONS.first()) }

    Column(modifier = Modifier.fillMaxSize().background(Background).padding(24.dp)) {
        Text("Add habit", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 24.sp)
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Habit name", color = TextSecondary) },
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
        Text("Pick an icon", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.height(120.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(EMOJI_OPTIONS) { option ->
                val selected = option == emoji
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(if (selected) IndigoPrimary else Surface, RoundedCornerShape(12.dp))
                        .clickableNoRipple { emoji = option },
                    contentAlignment = Alignment.Center
                ) {
                    Text(option, fontSize = 20.sp)
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Frequency", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            HabitFrequency.entries.forEach { option ->
                val selected = option == frequency
                Box(
                    modifier = Modifier
                        .background(if (selected) IndigoPrimary else Surface, RoundedCornerShape(99.dp))
                        .border(1.dp, if (selected) IndigoPrimary else Border, RoundedCornerShape(99.dp))
                        .clickableNoRipple { frequency = option }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        option.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = if (selected) Background else TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))
        Text("Color", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            TINT_OPTIONS.forEach { hex ->
                val color = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(hex))
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(color, CircleShape)
                        .border(if (tint == hex) 3.dp else 0.dp, TextPrimary, CircleShape)
                        .clickableNoRipple { tint = hex }
                )
            }
        }

        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(if (name.isNotBlank()) IndigoPrimary else Border, RoundedCornerShape(99.dp))
                .clickableNoRipple {
                    if (name.isNotBlank()) {
                        viewModel.createHabit(name, emoji, frequency, tint, onDone)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text("Create habit", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        }
    }
}
