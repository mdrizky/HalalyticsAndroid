package com.example.halalyticscompose.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════
// MINIMALIST PROFESSIONAL — Navy + Mint Medical Palette
// Terinspirasi dari GoPay, Dana, BRImo
// ═══════════════════════════════════════════════════════════════════

// Primary Palette — Deep Medical Navy
val Navy = Color(0xFF0D47A1)
val NavyDark = Color(0xFF1A237E)
val NavyLight = Color(0xFF1565C0)

// Accent — Soft Mint Green (kesan sehat/segar)
val MintAccent = Color(0xFF4DB6AC)
val MintLight = Color(0xFFE0F7FA)

// Backgrounds
val BackgroundLight = Color(0xFFF5F7FA)     // Abu sangat muda — bersih & elegan
val SurfaceWhite = Color(0xFFFFFFFF)        // Putih murni untuk kartu

// Borders
val BorderLight = Color(0xFFE0E0E0)         // Border abu tipis untuk kartu clean
val BorderLighter = Color(0xFFEEEEEE)       // Border sangat tipis

// Text
val TextPrimary = Color(0xFF212121)          // Hampir hitam
val TextSecondary = Color(0xFF757575)        // Abu medium
val TextTertiary = Color(0xFF9E9E9E)         // Abu muda
val TextOnNavy = Color(0xFFFFFFFF)           // Putih di atas Navy

// Semantic — Status Halal (tetap dipertahankan)
val HalalGreen = Color(0xFF2E7D32)          // Hijau profesional (bukan neon)
val HaramRed = Color(0xFFD32F2F)            // Merah medis
val MushboohYellow = Color(0xFFF57C00)      // Oranye tua (bukan kuning cerah)

// Functional
val SuccessGreen = Color(0xFF388E3C)
val WarningAmber = Color(0xFFF57C00)
val ErrorRed = Color(0xFFD32F2F)
val InfoBlue = Navy

// Shimmer / Skeleton Loading
val ShimmerBase = Color(0xFFE0E0E0)
val ShimmerHighlight = Color(0xFFF5F5F5)

// Legacy compatibility (mapping ke palet baru)
val LightBackground = BackgroundLight
val LightCard = SurfaceWhite
val DarkBackground = Color(0xFF121212)
val DarkCard = Color(0xFF1E1E1E)
val TextWhite = Color(0xFFF8FAFC)
val TextGray = TextSecondary
val TextMuted = TextTertiary
val TextDark = TextPrimary
val DarkBorder = Color(0xFF333333)
val DarkCardLight = Color(0xFF243244)
val HalalGreenDark = Color(0xFF1B5E20)
val TextGrayDark = Color(0xFF6B7280)
val TextMutedDark = Color(0xFF4B5563)

// Legacy vars mapping
val HalalColor = HalalGreen
val HaramColor = HaramRed
val MushboohColor = MushboohYellow
val PrimaryGreen = HalalGreen
val PrimaryColor = Navy
val SecondaryColor = MintAccent
val SuccessColor = SuccessGreen
val InfoColor = Navy
val WarningColor = WarningAmber
val ErrorColor = ErrorRed
val DangerColor = HaramRed

// Premium Emerald (kept for backward compat, but mapped to new values)
val Emerald500 = HalalGreen
val Emerald600 = Color(0xFF2E7D32)
val Emerald700 = Color(0xFF1B5E20)
val Emerald900 = Color(0xFF064E3B)

// Premium Dark Surface (kept for backward compat)
val BgDarkBase = DarkBackground
val BgDarkSurface = DarkCard
val BgDarkElevated = Color(0xFF334155)

// Glass Components
val GlassWhite = Color(0x1AFFFFFF)
val GlassBorder = Color(0x14FFFFFF)
