package com.cycleone.cycleoneapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.cycleone.cycleoneapp.R

val monsterratFamily = FontFamily(
    Font(R.font.montserrat_thin, FontWeight.Thin),
    Font(R.font.montserrat_extra_light, FontWeight.ExtraLight),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semi_bold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_extra_bold, FontWeight.ExtraBold),
    Font(R.font.montserrat_black, FontWeight.Black),
)

val monsterratAlternatesFamily = FontFamily(
    Font(R.font.montserrat_alternates_thin, FontWeight.Thin),
    Font(R.font.montserrat_alternates_extra_light, FontWeight.ExtraLight),
    Font(R.font.montserrat_alternates_light, FontWeight.Light),
    Font(R.font.montserrat_alternates_regular, FontWeight.Normal),
    Font(R.font.montserrat_alternates_medium, FontWeight.Medium),
    Font(R.font.montserrat_alternates_semi_bold, FontWeight.SemiBold),
    Font(R.font.montserrat_alternates_bold, FontWeight.Bold),
    Font(R.font.montserrat_alternates_extra_bold, FontWeight.ExtraBold),
    Font(R.font.montserrat_alternates_black, FontWeight.Black),
)

val MondaFamily = FontFamily(
    Font(R.font.monda_regular, FontWeight.Normal),
    Font(R.font.monda_medium, FontWeight.Medium),
    Font(R.font.monda_bold, FontWeight.Bold),
    Font(R.font.monda_semi_bold, FontWeight.SemiBold),
)

val typography = Typography(
    displayLarge = TextStyle(
        fontFamily = monsterratAlternatesFamily,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 48.sp,
        lineHeight = 54.sp
    ),
    titleMedium = TextStyle(
        fontFamily = monsterratFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 40.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = monsterratFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        lineHeight = 25.sp,
        textAlign = TextAlign.Justify
    ),
    labelLarge = TextStyle(
        fontFamily = monsterratFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = monsterratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 18.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = monsterratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp,
        lineHeight = 25.sp
    )
)
