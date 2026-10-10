package com.example.tripzyfrontend.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Tripzy Design System: Dark Midnight Theme
 * The app is exclusively designed in dark midnight (#0B1326) with refined glassmorphism,
 * glowing teal cyan, and royal blue accents. Light mode has been removed.
 */
private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    primaryContainer = MidnightSurfaceContainer,
    onPrimaryContainer = DarkTextPrimary,
    secondary = BrandSecondary,
    onSecondary = Color.White,
    secondaryContainer = MidnightSurfaceHigh,
    onSecondaryContainer = DarkTextPrimary,
    background = MidnightBackground,
    onBackground = DarkTextPrimary,
    surface = MidnightSurfaceLow,
    onSurface = DarkTextPrimary,
    surfaceVariant = MidnightSurfaceContainer,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    error = BalanceNegative,
    onError = Color.White
)

@Composable
fun TripzyTheme(
    darkTheme: Boolean = true, // Maintained for backward compatibility; pure dark midnight enforced
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}