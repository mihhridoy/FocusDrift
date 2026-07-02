package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.ui.theme.DmMono
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun QuickStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = TextSecondary, modifier = Modifier.padding(end = 4.dp))
            Text(label, color = TextSecondary, fontSize = 11.sp)
        }
        Text(value, color = valueColor, fontFamily = DmMono, fontWeight = FontWeight.Medium, fontSize = 18.sp)
    }
}
