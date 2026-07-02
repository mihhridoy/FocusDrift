package com.radonshadow.focusdrift.ui.screens.subscription

import android.app.Activity
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.constants.SubscriptionConstants
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoLight
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.Surface
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary

private data class ProFeature(val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)

private val PRO_FEATURES = listOf(
    ProFeature(Icons.Filled.Bolt, "Unlimited focus sessions (free = 5/day)"),
    ProFeature(Icons.Filled.Public, "Unlimited body double rooms"),
    ProFeature(Icons.Filled.LocalFireDepartment, "Extended streak protection (3 grace days)"),
    ProFeature(Icons.Filled.Palette, "All orb skins and themes unlocked"),
    ProFeature(Icons.Filled.QueryStats, "Advanced focus analytics"),
    ProFeature(Icons.Filled.NotificationsActive, "Smart ADHD reminders (time-blind mode)"),
    ProFeature(Icons.Filled.Handshake, "Priority body double matching")
)

@Composable
fun SubscriptionScreen(
    onDismiss: () -> Unit,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val status by viewModel.status.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 26.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), horizontalArrangement = Arrangement.End) {
            Text("✕", color = TextSecondary, fontSize = 16.sp, modifier = Modifier.clickableNoRipple(onDismiss))
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                "FocusDrift PRO",
                color = TextPrimary,
                fontFamily = Nunito,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Spacer(Modifier.height(14.dp))
            Text(
                "Your brain deserves\nbetter tools.",
                color = TextPrimary,
                fontFamily = Nunito,
                fontWeight = FontWeight.Black,
                fontSize = 26.sp,
                textAlign = TextAlign.Center,
                lineHeight = 30.sp
            )
            Spacer(Modifier.height(8.dp))
            Text("FocusDrift Pro unlocks everything.", color = TextSecondary, fontSize = 14.sp)
        }

        Spacer(Modifier.height(20.dp))
        if (status.isPro) {
            Box(
                modifier = Modifier.fillMaxWidth().background(Surface, RoundedCornerShape(16.dp)).padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("You're already Pro — thank you!", color = AmberReward, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(20.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            PRO_FEATURES.forEach { feature ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(feature.icon, contentDescription = null, tint = TextPrimary, modifier = Modifier.padding(end = 12.dp))
                    Text(feature.label, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.5.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            PricingCard(
                label = "Monthly",
                price = SubscriptionConstants.PRICE_MONTHLY_DISPLAY,
                highlighted = false,
                modifier = Modifier.weight(1f),
                onClick = { activity?.let { viewModel.purchase(it, SubscriptionConstants.PRODUCT_MONTHLY) } }
            )
            PricingCard(
                label = "Yearly",
                price = SubscriptionConstants.PRICE_YEARLY_DISPLAY,
                highlighted = true,
                modifier = Modifier.weight(1f),
                onClick = { activity?.let { viewModel.purchase(it, SubscriptionConstants.PRODUCT_YEARLY) } }
            )
            PricingCard(
                label = "Lifetime",
                price = SubscriptionConstants.PRICE_LIFETIME_DISPLAY,
                highlighted = false,
                modifier = Modifier.weight(1f),
                onClick = { activity?.let { viewModel.purchase(it, SubscriptionConstants.PRODUCT_LIFETIME) } }
            )
        }

        Spacer(Modifier.height(24.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    androidx.compose.ui.graphics.Brush.horizontalGradient(listOf(IndigoPrimary, IndigoLight)),
                    RoundedCornerShape(99.dp)
                )
                .clickableNoRipple { activity?.let { viewModel.startFreeTrial(it) } },
            contentAlignment = Alignment.Center
        ) {
            Text("Start 7-day free trial", color = Background, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Cancel anytime. No hidden fees.",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))
    }
}

@Composable
private fun PricingCard(label: String, price: String, highlighted: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .background(Surface, RoundedCornerShape(16.dp))
            .border(if (highlighted) 2.dp else 1.dp, if (highlighted) AmberReward else Border, RoundedCornerShape(16.dp))
            .clickableNoRipple(onClick)
            .padding(vertical = 14.dp, horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (highlighted) {
                Text(
                    "BEST VALUE",
                    color = Background,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.background(AmberReward, RoundedCornerShape(99.dp)).padding(horizontal = 8.dp, vertical = 2.dp)
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(label, color = TextSecondary, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(price, color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.Black, fontSize = 18.sp)
        }
    }
}
