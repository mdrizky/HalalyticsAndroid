package com.example.halalyticscompose.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════════════════════
// EMERALD FOREST — Premium Medical & Halal Palette
// Deep Teal/Emerald for trust + Modern Mint for freshness
// ═══════════════════════════════════════════════════════════════════

// Primary Palette — Deep Emerald (Kepercayaan & Stabilitas)
val Navy = Color(0xFF004D40)           // Deep Teal/Emerald — primary utama
val NavyDark = Color(0xFF00332B)       // Darker emerald for emphasis
val NavyLight = Color(0xFF00695C)      // Slightly lighter emerald

// Accent — Modern Mint Green (Segar & Modern)
val MintAccent = Color(0xFF26A69A)     // Modern Mint — buttons, icons
val MintLight = Color(0xFFE0F2F1)      // Soft Sage — card backgrounds

// Gold — Premium Accent (untuk badge HALAL PREMIUM)
val GoldAccent = Color(0xFFD4AF37)     // Gold premium
val GoldLight = Color(0xFFFFF8E1)      // Gold pale

// Backgrounds
val BackgroundLight = Color(0xFFF4F9F8)     // Off-White Green — bersih & luas
val SurfaceWhite = Color(0xFFFFFFFF)        // Putih murni untuk kartu

// Borders
val BorderLight = Color(0xFFE0E0E0)         // Border abu tipis untuk kartu clean
val BorderLighter = Color(0xFFEEEEEE)       // Border sangat tipis

// Text
val TextPrimary = Color(0xFF212121)          // Hampir hitam
val TextSecondary = Color(0xFF757575)        // Abu medium
val TextTertiary = Color(0xFF9E9E9E)         // Abu muda
val TextOnNavy = Color(0xFFFFFFFF)           // Putih di atas Emerald

// Semantic — Status Halal (tetap dipertahankan)
val HalalGreen = Color(0xFF2E7D32)          // Hijau profesional (bukan neon)
val HaramRed = Color(0xFFD32F2F)            // Merah medis
val MushboohYellow = Color(0xFFF57C00)      // Oranye tua (bukan kuning cerah)

// Functional
val SuccessGreen = Color(0xFF388E3C)
val WarningAmber = Color(0xFFF57C00)
val ErrorRed = Color(0xFFD32F2F)
val InfoBlue = Color(0xFF004D40)            // Mapped to emerald

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
val DarkCardLight = Color(0xFF1A3330)       // Dark emerald card
val HalalGreenDark = Color(0xFF1B5E20)
val TextGrayDark = Color(0xFF6B7280)
val TextMutedDark = Color(0xFF4B5563)

// Mappings for HomeScreen compatibility
val TextMedium = TextSecondary
val TextLight = TextTertiary
val CardWhite = SurfaceWhite
val Mint = MintAccent
val MintPale = MintLight
val BorderGray = BorderLight
val Gold = GoldAccent

// Legacy vars mapping
val HalalColor = HalalGreen
val HaramColor = HaramRed
val MushboohColor = MushboohYellow
val PrimaryGreen = Color(0xFF004D40)        // Mapped to emerald
val PrimaryColor = Navy
val SecondaryColor = MintAccent
val SuccessColor = SuccessGreen
val InfoColor = Navy
val WarningColor = WarningAmber
val ErrorColor = ErrorRed
val DangerColor = HaramRed

// Premium Emerald (updated for new palette)
val Emerald500 = Color(0xFF004D40)
val Emerald600 = Color(0xFF00695C)
val Emerald700 = Color(0xFF004D40)
val Emerald900 = Color(0xFF00332B)

// Premium Dark Surface
val BgDarkBase = DarkBackground
val BgDarkSurface = DarkCard
val BgDarkElevated = Color(0xFF1A3330)

// Glass Components
val GlassWhite = Color(0x1AFFFFFF)
val GlassBorder = Color(0x14FFFFFF)
