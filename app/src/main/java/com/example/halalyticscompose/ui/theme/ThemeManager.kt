package com.example.halalyticscompose.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

// Theme data class
data class ThemePreferences(
    val isDarkMode: Boolean = false
)

// Simple theme manager without DataStore for now
object ThemeManager {
    val themeFlow: Flow<ThemePreferences> = flow {
        emit(ThemePreferences(isDarkMode = false))
    }

    suspend fun setDarkMode(isDark: Boolean) {
        println("Theme changed to dark mode: $isDark")
    }

    suspend fun getDarkMode(): Boolean {
        return false
    }
}

// ═══════════════════════════════════════════════════════════════════
// LIGHT COLOR SCHEME — Minimalist Professional
// Navy (#0D47A1) + Mint (#4DB6AC) + White Surface + #F5F7FA Background
// ═══════════════════════════════════════════════════════════════════
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0D47A1),               // Deep Navy — primary buttons, FAB, icons
    onPrimary = Color(0xFFFFFFFF),             // White on navy
    primaryContainer = Color(0xFFE3F2FD),      // Light navy wash
    onPrimaryContainer = Color(0xFF0D47A1),    // Navy text on container

    secondary = Color(0xFF4DB6AC),             // Mint accent
    onSecondary = Color(0xFFFFFFFF),           // White on mint
    secondaryContainer = Color(0xFFE0F7FA),    // Mint very light
    onSecondaryContainer = Color(0xFF00695C),  // Dark teal on container

    tertiary = Color(0xFF4DB6AC),              // Mint (same as secondary for consistency)
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE0F7FA),
    onTertiaryContainer = Color(0xFF004D40),

    error = Color(0xFFD32F2F),                 // Medical red
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),

    background = Color(0xFFF5F7FA),            // Light gray — clean & elegant
    onBackground = Color(0xFF212121),          // Almost black text

    surface = Color(0xFFFFFFFF),               // Pure white cards
    onSurface = Color(0xFF212121),             // Dark text on white
    
    surfaceVariant = Color(0xFFF0F2F5),        // Slightly darker surface for contrast
    onSurfaceVariant = Color(0xFF757575),      // Secondary text

    outline = Color(0xFFE0E0E0),               // Light border
    outlineVariant = Color(0xFFEEEEEE),        // Very light border

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF303030),
    inverseOnSurface = Color(0xFFF5F5F5),
    inversePrimary = Color(0xFF90CAF9)         // Light blue for inverted
)

// ═══════════════════════════════════════════════════════════════════
// DARK COLOR SCHEME — Professional Dark Mode
// ═══════════════════════════════════════════════════════════════════
val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),               // Light blue for dark mode
    onPrimary = Color(0xFF0D47A1),             // Navy text on light primary
    primaryContainer = Color(0xFF0D47A1),      // Navy container
    onPrimaryContainer = Color(0xFFE3F2FD),    // Light on navy

    secondary = Color(0xFF80CBC4),             // Mint lighter for dark
    onSecondary = Color(0xFF00332F),
    secondaryContainer = Color(0xFF004D40),
    onSecondaryContainer = Color(0xFFE0F7FA),

    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF00332F),
    tertiaryContainer = Color(0xFF004D40),

    error = Color(0xFFFF8A80),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),

    background = Color(0xFF121212),            // Very dark
    onBackground = Color(0xFFECECEC),          // Light text

    surface = Color(0xFF1E1E1E),               // Dark surface
    onSurface = Color(0xFFECECEC),
    
    surfaceVariant = Color(0xFF2C2C2C),
    onSurfaceVariant = Color(0xFFB0B0B0),

    outline = Color(0xFF444444),
    outlineVariant = Color(0xFF333333),

    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE3F2FD),
    inverseOnSurface = Color(0xFF1C1B1F),
    inversePrimary = Color(0xFF0D47A1)
)

// ═══════════════════════════════════════════════════════════════════
// CUSTOM COLORS (accessible directly, without MaterialTheme)
// ═══════════════════════════════════════════════════════════════════
object CustomColors {
    // Light theme
    val LightTextPrimary = Color(0xFF212121)
    val LightTextSecondary = Color(0xFF757575)
    val LightTextTertiary = Color(0xFF9E9E9E)
    val LightBackground = Color(0xFFF5F7FA)
    val LightSurface = Color(0xFFFFFFFF)
    val LightBorder = Color(0xFFE0E0E0)
    val LightCard = Color(0xFFFFFFFF)
    val LightIcon = Color(0xFF0D47A1)        // Navy icons in light mode

    // Dark theme
    val DarkTextPrimary = Color(0xFFFFFFFF)
    val DarkTextSecondary = Color(0xFFB0B0B0)
    val DarkTextTertiary = Color(0xFF808080)
    val DarkBackground = Color(0xFF121212)
    val DarkSurface = Color(0xFF1E1E1E)
    val DarkBorder = Color(0xFF333333)
    val DarkCard = Color(0xFF2A2A2A)
    val DarkIcon = Color(0xFF90CAF9)         // Light blue icons in dark mode

    // Status colors (work for both themes)
    val Success = Color(0xFF388E3C)           // Professional green
    val Warning = Color(0xFFF57C00)           // Deep orange
    val Error = Color(0xFFD32F2F)             // Medical red
    val Info = Color(0xFF0D47A1)              // Navy
}

// Theme utilities
object ThemeUtils {
    fun getTextColor(isDark: Boolean): Color =
        if (isDark) CustomColors.DarkTextPrimary else CustomColors.LightTextPrimary

    fun getSecondaryTextColor(isDark: Boolean): Color =
        if (isDark) CustomColors.DarkTextSecondary else CustomColors.LightTextSecondary

    fun getBackgroundColor(isDark: Boolean): Color =
        if (isDark) CustomColors.DarkBackground else CustomColors.LightBackground

    fun getSurfaceColor(isDark: Boolean): Color =
        if (isDark) CustomColors.DarkSurface else CustomColors.LightSurface

    fun getBorderColor(isDark: Boolean): Color =
        if (isDark) CustomColors.DarkBorder else CustomColors.LightBorder

    fun getCardColor(isDark: Boolean): Color =
        if (isDark) CustomColors.DarkCard else CustomColors.LightCard

    fun getIconColor(isDark: Boolean): Color =
        if (isDark) CustomColors.DarkIcon else CustomColors.LightIcon
}
