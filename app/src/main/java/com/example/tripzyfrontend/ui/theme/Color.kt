package com.example.tripzyfrontend.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Primary Brand Palette (Derived from Tripzy Logo)
val BrandPrimary = Color(0xFF008EA5) // Deep Teal Cyan
val BrandPrimaryLight = Color(0xFF00A7C2)
val BrandPrimaryDark = Color(0xFF006B7D)

val BrandSecondary = Color(0xFF2F7BF6) // Vibrant Royal Blue
val BrandSecondaryLight = Color(0xFF5B97F8)
val BrandSecondaryDark = Color(0xFF1B62D6)

// Brand Gradients & Tints
val BrandGradientBrush = Brush.horizontalGradient(
    colors = listOf(BrandPrimary, BrandSecondary)
)
// Balance Roles (Dark Midnight Translucent Tints)
val BalancePositive = Color(0xFF10B981) // Emerald: "You'll receive"
val BalancePositiveBg = Color(0x2E10B981)
val BalancePositiveDark = Color(0xFF059669)

val BalanceNegative = Color(0xFFEF4444) // Crimson: "You owe"
val BalanceNegativeBg = Color(0x2EEF4444)
val BalanceNegativeDark = Color(0xFFDC2626)

val BalanceZero = Color(0xFF94A3B8) // Slate: "Settled"
val BalanceZeroBg = Color(0x2E64748B)

// Expense Type Tags
val TagPersonalText = Color(0xFFFBBF24)
val TagPersonalBg = Color(0x2EF59E0B)

val TagSharedText = Color(0xFF6DD4ED)
val TagSharedBg = Color(0x2E008EA5)

// Tour Lifecycle Status Colors
val StatusActiveText = Color(0xFF6DD4ED)
val StatusActiveBg = Color(0x2E008EA5)

val StatusSettledText = Color(0xFF10B981)
val StatusSettledBg = Color(0x2E10B981)

val StatusArchivedText = Color(0xFF94A3B8)
val StatusArchivedBg = Color(0x2E64748B)


// Neutral Foundation (Dark Mode)
val DarkBackground = Color(0xFF0B1326) // Stitch Midnight canvas
val DarkSurface = Color(0xFF0B1326)
val DarkSurfaceVariant = Color(0xFF2D3449)
val DarkTextPrimary = Color(0xFFDAE2FD)
val DarkTextSecondary = Color(0xFFBDC8CC)
val DarkTextTertiary = Color(0xFF879396)
val DarkBorder = Color(0xFF3E494C)

// Stitch Midnight Design System Containers
val MidnightBackground = Color(0xFF0B1326)
val MidnightSurfaceLowest = Color(0xFF060E20)
val MidnightSurfaceLow = Color(0xFF131B2E)
val MidnightSurfaceContainer = Color(0xFF171F33)
val MidnightSurfaceHigh = Color(0xFF222A3D)
val MidnightSurfaceHighest = Color(0xFF2D3449)
val MidnightSurfaceBright = Color(0xFF31394D)

// Glassmorphism Tokens
val GlassBorder = Color(0x26FFFFFF) // 15% white border
val GlassBorderTeal = Color(0x66008EA5) // Teal accent border
val GlassSurfaceDark = Color(0xB3171F33) // 70% midnight container
val GlassSurfaceCard = Color(0x99222A3D) // 60% high container

val NeonTeal = Color(0xFF6DD4ED)
val NeonBlue = Color(0xFFAEC6FF)

val PrimaryCtaGradient = Brush.horizontalGradient(
    colors = listOf(Color(0xFF008EA5), Color(0xFF2F7BF6))
)
val GlassCardGradient = Brush.linearGradient(
    colors = listOf(Color(0x2E008EA5), Color(0x1A2F7BF6))
)