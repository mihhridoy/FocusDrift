package com.radonshadow.focusdrift.ui.screens.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.domain.model.ShopCategory
import com.radonshadow.focusdrift.ui.components.ShopItemCard
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.DmMono
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

@Composable
fun ShopScreen(viewModel: ShopViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    val purchaseMessage by viewModel.purchaseMessageFlow.collectAsState()

    LaunchedEffect(purchaseMessage) {
        if (purchaseMessage != null) {
            delay(2500)
            viewModel.clearPurchaseMessage()
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        if (purchaseMessage != null) {
            Text(
                purchaseMessage.orEmpty(),
                color = AmberReward,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Focus Shop", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 24.sp)
            Row(
                modifier = Modifier.background(SurfaceElevated, RoundedCornerShape(99.dp)).padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = AmberReward, modifier = Modifier.padding(end = 6.dp))
                Text("${uiState.coins}", color = AmberReward, fontFamily = DmMono, fontWeight = FontWeight.Medium, fontSize = 16.sp)
            }
        }

        Row(
            modifier = Modifier.padding(horizontal = 22.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ShopCategory.entries.forEach { category ->
                val selected = category == uiState.selectedCategory
                Row(
                    modifier = Modifier
                        .background(if (selected) IndigoPrimary else Surface, RoundedCornerShape(99.dp))
                        .border(1.dp, if (selected) IndigoPrimary else Border, RoundedCornerShape(99.dp))
                        .clickableNoRipple { viewModel.selectCategory(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        category.name.split("_").joinToString(" ") { it.lowercase().replaceFirstChar(Char::uppercase) },
                        color = if (selected) Background else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(22.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(uiState.items, key = { it.id }) { item ->
                ShopItemCard(
                    item = item,
                    isOwned = item.id in uiState.unlockedItemIds,
                    onClick = { viewModel.purchase(item.id) }
                )
            }
        }
    }
}
