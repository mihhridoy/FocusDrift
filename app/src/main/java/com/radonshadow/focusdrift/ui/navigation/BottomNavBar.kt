package com.radonshadow.focusdrift.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fireplace
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GroupWork
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.SurfaceNav
import com.radonshadow.focusdrift.ui.theme.TextSecondary

private data class NavItem(val screen: Screen, val label: String, val icon: ImageVector)

private val NAV_ITEMS = listOf(
    NavItem(Screen.Home, "Home", Icons.Filled.Home),
    NavItem(Screen.Timer, "Focus", Icons.Filled.GpsFixed),
    NavItem(Screen.RoomsList, "Rooms", Icons.Filled.GroupWork),
    NavItem(Screen.Habits, "Habits", Icons.Filled.Fireplace),
    NavItem(Screen.Profile, "Profile", Icons.Filled.Person)
)

@Composable
fun BottomNavBar(
    currentRoute: String?,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(74.dp)
            .background(SurfaceNav)
            .border(width = 1.dp, color = Border)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        NAV_ITEMS.forEach { item ->
            val selected = currentRoute == item.screen.route
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickableNoRipple { onNavigate(item.screen) }
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selected) IndigoPrimary else TextSecondary
                )
                Text(
                    text = item.label,
                    color = if (selected) IndigoPrimary else TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                )
            }
        }
    }
}
