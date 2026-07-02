@file:OptIn(ExperimentalTextApi::class)

package com.radonshadow.focusdrift.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.radonshadow.focusdrift.R

private fun variableWeight(weight: Int) = FontVariation.Settings(FontVariation.weight(weight))

val Nunito = FontFamily(
    Font(R.font.nunito_variable, FontWeight.Normal, variationSettings = variableWeight(400)),
    Font(R.font.nunito_variable, FontWeight.SemiBold, variationSettings = variableWeight(600)),
    Font(R.font.nunito_variable, FontWeight.Bold, variationSettings = variableWeight(700)),
    Font(R.font.nunito_variable, FontWeight.ExtraBold, variationSettings = variableWeight(800)),
    Font(R.font.nunito_variable, FontWeight.Black, variationSettings = variableWeight(900))
)

val Inter = FontFamily(
    Font(R.font.inter_variable, FontWeight.Normal, variationSettings = variableWeight(400)),
    Font(R.font.inter_variable, FontWeight.Medium, variationSettings = variableWeight(500)),
    Font(R.font.inter_variable, FontWeight.SemiBold, variationSettings = variableWeight(600)),
    Font(R.font.inter_variable, FontWeight.Bold, variationSettings = variableWeight(700))
)

val DmMono = FontFamily(
    Font(R.font.dm_mono_regular, FontWeight.Normal),
    Font(R.font.dm_mono_regular, FontWeight.Medium)
)

val FocusDriftTypography = Typography(
    displayMedium = TextStyle(
        fontFamily = DmMono,
        fontWeight = FontWeight.Medium,
        fontSize = 52.sp,
        letterSpacing = (-0.02).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Black,
        fontSize = 30.sp,
        letterSpacing = (-0.01).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = Nunito,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 17.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp
    )
)
