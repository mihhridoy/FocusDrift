package com.radonshadow.focusdrift.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.domain.model.ShopItem
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.GreenSuccess
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TextPrimary

@Composable
fun ShopItemCard(
    item: ShopItem,
    isOwned: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val previewColor = item.previewColor?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() }
    val glowColor = item.glowColor?.let { runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull() } ?: previewColor

    val borderColor = when {
        isOwned -> GreenSuccess
        item.isSpecialEvent -> AmberReward
        else -> Border
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickableNoRipple(onClick)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(
                    brush = if (previewColor != null && glowColor != null) {
                        Brush.radialGradient(listOf(previewColor, glowColor))
                    } else {
                        Brush.radialGradient(listOf(Surface, Surface))
                    },
                    shape = RoundedCornerShape(14.dp)
                )
        ) {}

        Spacer(Modifier.height(10.dp))
        Text(item.name, color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            when {
                isOwned -> {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = GreenSuccess, modifier = Modifier.height(14.dp))
                    Text(" Owned", color = GreenSuccess, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
                item.isSpecialEvent -> {
                    Text("Special event", color = AmberReward, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                }
                else -> {
                    Icon(Icons.Filled.Lock, contentDescription = null, tint = AmberReward, modifier = Modifier.height(13.dp))
                    Icon(Icons.Filled.MonetizationOn, contentDescription = null, tint = AmberReward, modifier = Modifier.height(13.dp))
                    Text(" ${item.costCoins}", color = AmberReward, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                }
            }
        }
    }
}
