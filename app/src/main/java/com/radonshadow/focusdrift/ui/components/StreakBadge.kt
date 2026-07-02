package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated

enum class StreakBadgeSize(val fontSize: Int, val iconSize: Int, val horizontalPadding: Int, val verticalPadding: Int) {
    SMALL(11, 12, 9, 4),
    MEDIUM(14, 15, 12, 6),
    LARGE(18, 19, 16, 8)
}

@Composable
fun StreakBadge(
    days: Int,
    modifier: Modifier = Modifier,
    size: StreakBadgeSize = StreakBadgeSize.MEDIUM
) {
    Row(
        modifier = modifier
            .background(SurfaceElevated, shape = androidx.compose.foundation.shape.RoundedCornerShape(99.dp))
            .padding(horizontal = size.horizontalPadding.dp, vertical = size.verticalPadding.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = AmberReward,
            modifier = Modifier.size(size.iconSize.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = "$days",
            color = AmberReward,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = size.fontSize.sp
        )
    }
}
