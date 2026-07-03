package com.radonshadow.focusdrift.ui.screens.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.radonshadow.focusdrift.core.extensions.clickableNoRipple
import com.radonshadow.focusdrift.ui.components.FocusOrb
import com.radonshadow.focusdrift.ui.components.FocusOrbState
import com.radonshadow.focusdrift.ui.theme.AmberReward
import com.radonshadow.focusdrift.ui.theme.Background
import com.radonshadow.focusdrift.ui.theme.Border
import com.radonshadow.focusdrift.ui.theme.IndigoPrimary
import com.radonshadow.focusdrift.ui.theme.Nunito
import com.radonshadow.focusdrift.ui.theme.SurfaceElevated
import com.radonshadow.focusdrift.ui.theme.TealRooms
import com.radonshadow.focusdrift.ui.theme.TextPrimary
import com.radonshadow.focusdrift.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private data class OnboardingSlide(val accent: androidx.compose.ui.graphics.Color, val headline: String, val body: String)

private val SLIDES = listOf(
    OnboardingSlide(IndigoPrimary, "Focus in bursts.\nRest without guilt.", "25-minute focus sessions designed for ADHD brains. No judgment when you drift — just come back."),
    OnboardingSlide(TealRooms, "Work alongside\nreal people.", "Join live body doubling rooms. Strangers on the internet keeping each other accountable. It works."),
    OnboardingSlide(AmberReward, "Every session\nearns you something.", "Streaks, XP, focus coins, and level-ups. Your dopamine system finally has something to work with.")
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val pagerState = rememberPagerState(pageCount = { SLIDES.size })
    val scope = rememberCoroutineScope()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp).height(54.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("9:41", color = TextPrimary, fontFamily = Nunito, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            Text(
                "Skip",
                color = TextSecondary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                modifier = Modifier.clickableNoRipple { viewModel.completeOnboarding(onFinished) }
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f).fillMaxWidth()
        ) { page ->
            OnboardingSlideContent(page)
        }

        Column(modifier = Modifier.padding(horizontal = 36.dp, vertical = 26.dp)) {
            PagerDots(pagerState, SLIDES.map { it.accent })

            if (errorMessage != null) {
                Spacer(Modifier.height(12.dp))
                Text(
                    errorMessage.orEmpty(),
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(26.dp))

            val isLastPage = pagerState.currentPage == SLIDES.lastIndex
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(IndigoPrimary, RoundedCornerShape(99.dp))
                    .clickableNoRipple {
                        if (isLastPage) {
                            viewModel.completeOnboarding(onFinished)
                        } else {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    if (isLastPage) "Let's go" else "Next",
                    color = Background,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PagerDots(pagerState: PagerState, colors: List<androidx.compose.ui.graphics.Color>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        colors.forEachIndexed { index, color ->
            val isActive = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(8.dp)
                    .width(if (isActive) 26.dp else 8.dp)
                    .background(if (isActive) color else Border, RoundedCornerShape(99.dp))
            )
        }
    }
}

@Composable
private fun OnboardingSlideContent(page: Int) {
    val slide = SLIDES[page]
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (page) {
            0 -> FocusOrb(state = FocusOrbState.IDLE, size = 220.dp) {
                Text("25:00", color = TextPrimary, fontFamily = com.radonshadow.focusdrift.ui.theme.DmMono, fontSize = 52.sp)
            }
            1 -> AvatarTrio()
            2 -> StreakXpVisual()
        }

        Spacer(Modifier.height(40.dp))
        Text(
            slide.headline,
            color = TextPrimary,
            fontFamily = Nunito,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 27.sp,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp
        )
        Spacer(Modifier.height(16.dp))
        Text(
            slide.body,
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 23.sp
        )
    }
}

@Composable
private fun AvatarTrio() {
    Box(modifier = Modifier.size(width = 230.dp, height = 150.dp), contentAlignment = Alignment.Center) {
        Row {
            listOf(TealRooms, IndigoPrimary, AmberReward).forEachIndexed { index, color ->
                Box(
                    modifier = Modifier
                        .padding(start = if (index == 0) 0.dp else (index * -14).dp)
                        .size(if (index == 0) 74.dp else 64.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Person, contentDescription = null, tint = Background)
                }
            }
        }
    }
}

@Composable
private fun StreakXpVisual() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Filled.LocalFireDepartment, contentDescription = null, tint = AmberReward, modifier = Modifier.size(72.dp))
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .size(width = 220.dp, height = 16.dp)
                .background(SurfaceElevated, RoundedCornerShape(99.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(16.dp)
                    .background(AmberReward, RoundedCornerShape(99.dp))
            )
        }
    }
}
