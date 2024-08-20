package com.cycleone.cycleoneapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xffff6b35),
    secondary = Color(0xfff7c59f),
    tertiary = Color(0xffefefd0),
    onPrimary = Color(0xffefefd0)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xff004e89),
    secondary = Color(0xff1a659e),
    tertiary = Color(0xffefefd0),
    onPrimary = Color(0xffefefd0)
)

private val shapes = Shapes(
    extraLarge = RoundedCornerShape(25.dp),
    large = RoundedCornerShape(20.dp),
    medium = RoundedCornerShape(10.dp),
)

@Composable
fun CycleoneAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable() Function0<Unit>
) {

    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }


    MaterialTheme(
        colorScheme,
        typography = typography,
        content = content
    )
}