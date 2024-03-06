package com.cycleone.cycleoneapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.R


val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val monsterratAlternates = GoogleFont("Montserrat Alternates")
val monsterrat= GoogleFont("Montserrat")

val monsterratAlternates700 = FontFamily(Font(fontProvider = provider, googleFont = monsterratAlternates, weight = FontWeight(700)))
val monsterratAlternates500 = FontFamily(Font(fontProvider = provider, googleFont = monsterratAlternates, weight = FontWeight(500)))
val monsterratAlternates400 = FontFamily(Font(fontProvider = provider, googleFont = monsterratAlternates, weight = FontWeight(400)))
val monsterrat400 = FontFamily(Font(fontProvider = provider, googleFont = monsterrat, weight = FontWeight(400)))
val monsterrat500 = FontFamily(Font(fontProvider = provider, googleFont = monsterrat, weight = FontWeight(500)))
val monsterrat700 = FontFamily(Font(fontProvider = provider, googleFont = monsterrat, weight = FontWeight(700)))

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = monsterrat700,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = monsterrat500,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = monsterrat400,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = monsterratAlternates700,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = monsterratAlternates500,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleSmall = TextStyle(
        fontFamily = monsterratAlternates400,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelLarge = TextStyle(
        fontFamily = monsterrat700,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = monsterrat500,
        fontWeight = FontWeight.Light,
        fontSize = 14.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = monsterrat400,
        fontWeight = FontWeight.Light,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)