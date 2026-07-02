package com.radonshadow.focusdrift.ui.screens.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun SetupScreen(
    onFinished: () -> Unit,
    viewModel: SetupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .padding(top = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(SurfaceElevated, RoundedCornerShape(99.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.75f)
                        .background(IndigoPrimary, RoundedCornerShape(99.dp))
                )
            }
            Spacer(Modifier.height(20.dp))
            Text(
                "Let's set up\nyour brain",
                color = TextPrimary,
                fontFamily = Nunito,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                lineHeight = 30.sp
            )

            Spacer(Modifier.height(26.dp))
            SectionLabel("When do you usually drift?")
            ChipFlowRow {
                DRIFT_TIME_OPTIONS.forEach { option ->
                    SelectableChip(
                        label = option.label,
                        selected = option.id in uiState.selectedDriftTimes,
                        onClick = { viewModel.toggleDriftTime(option.id) }
                    )
                }
            }

            Spacer(Modifier.height(26.dp))
            SectionLabel("What kills your focus most?")
            ChipFlowRow {
                FOCUS_KILLER_OPTIONS.forEach { option ->
                    SelectableChip(
                        label = option.label,
                        selected = option.id in uiState.selectedFocusKillers,
                        onClick = { viewModel.toggleFocusKiller(option.id) }
                    )
                }
            }

            Spacer(Modifier.height(26.dp))
            SectionLabel("How long before you drift?")
            Row {
                Text("Starting with ", color = TextSecondary, fontSize = 14.sp)
                Text("${uiState.focusMinutes}-minute", color = IndigoPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(" sessions", color = TextSecondary, fontSize = 14.sp)
            }
            Spacer(Modifier.height(6.dp))
            Slider(
                value = uiState.focusMinutes.toFloat(),
                onValueChange = { viewModel.setFocusMinutes(it.toInt()) },
                valueRange = 5f..45f,
                colors = SliderDefaults.colors(
                    thumbColor = TextPrimary,
                    activeTrackColor = IndigoPrimary,
                    inactiveTrackColor = SurfaceElevated
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("5min", color = TextSecondary, fontSize = 12.sp)
                Text("45min", color = TextSecondary, fontSize = 12.sp)
            }

            Spacer(Modifier.height(20.dp))
        }

        Box(modifier = Modifier.padding(horizontal = 28.dp, vertical = 24.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(IndigoPrimary, RoundedCornerShape(99.dp))
                    .clickableNoRipple { viewModel.finishSetup(onFinished) },
                contentAlignment = Alignment.Center
            ) {
                Text("Start my journey →", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 12.dp))
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun ChipFlowRow(content: @Composable () -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        content()
    }
}

@Composable
private fun SelectableChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (selected) IndigoPrimary else Surface, RoundedCornerShape(99.dp))
            .border(1.dp, if (selected) IndigoPrimary else Border, RoundedCornerShape(99.dp))
            .clickableNoRipple(onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp)
    ) {
        Text(
            label,
            color = if (selected) Background else TextSecondary,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold,
            fontSize = 14.sp
        )
    }
}
