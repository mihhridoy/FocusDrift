package com.radonshadow.focusdrift.ui.screens.habits

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.radonshadow.focusdrift.ui.components.HabitCard
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.TextPrimary

@Composable
fun HabitsScreen(
    onAddHabit: () -> Unit,
    viewModel: HabitsViewModel = hiltViewModel()
) {
    val habits by viewModel.habitsWithStreaks.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Your Habits", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Text("Add +", color = IndigoPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp, modifier = Modifier.clickableNoRipple(onAddHabit))
        }

        LazyColumn(
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 22.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(habits, key = { it.habit.id }) { item ->
                HabitCard(
                    habit = item.habit,
                    streakInfo = item.streak,
                    onMarkDone = { viewModel.markDone(item.habit.id) }
                )
            }
        }
    }
}
