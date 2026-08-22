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
val BrandTealTint = Color(0xFFE0F4F7)
val BrandBlueTint = Color(0xFFEBF3FE)

// Balance Roles (Crucial for instant scannability & financial trust)
val BalancePositive = Color(0xFF059669) // Emerald: "You'll receive"
val BalancePositiveBg = Color(0xFFECFDF5)
val BalancePositiveDark = Color(0xFF047857)

val BalanceNegative = Color(0xFFDC2626) // Crimson: "You owe"
val BalanceNegativeBg = Color(0xFFFEF2F2)
val BalanceNegativeDark = Color(0xFFB91C1C)

val BalanceZero = Color(0xFF64748B) // Slate: "Settled"
val BalanceZeroBg = Color(0xFFF1F5F9)

// Expense Type Tags
val TagPersonalText = Color(0xFFD97706)
val TagPersonalBg = Color(0xFFFEF3C7)

val TagSharedText = Color(0xFF008EA5)
val TagSharedBg = Color(0xFFE0F4F7)

// Tour Lifecycle Status Colors
val StatusActiveText = Color(0xFF008EA5)
val StatusActiveBg = Color(0xFFE0F4F7)

val StatusSettledText = Color(0xFF059669)
val StatusSettledBg = Color(0xFFD1FAE5)

val StatusArchivedText = Color(0xFF64748B)
val StatusArchivedBg = Color(0xFFF1F5F9)

// Neutral Foundation (Light Mode)
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextTertiary = Color(0xFF94A3B8)
val LightBorder = Color(0xFFE2E8F0)

// Neutral Foundation (Dark Mode)
val DarkBackground = Color(0xFF090E17)
val DarkSurface = Color(0xFF111A29)
val DarkSurfaceVariant = Color(0xFF1B283D)
val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextTertiary = Color(0xFF64748B)
val DarkBorder = Color(0xFF283A52)