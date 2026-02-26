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
    // Flow untuk observe theme changes (using system theme for now)
    val themeFlow: Flow<ThemePreferences> = flow {
        emit(ThemePreferences(isDarkMode = false)) // Default to light theme
    }

    // Save theme preference (placeholder)
    suspend fun setDarkMode(isDark: Boolean) {
        // TODO: Implement DataStore persistence
        // For now, just log the change
        println("Theme changed to dark mode: $isDark")
    }

    // Get current theme
    suspend fun getDarkMode(): Boolean {
        return false // Default to light theme
    }
}

// Custom color schemes for better contrast
val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1A237E),           // Deep blue
    onPrimary = Color(0xFFFFFFFF),           // White
    primaryContainer = Color(0xFFE3F2FD),      // Light blue
    onPrimaryContainer = Color(0xFF0D47A1),  // Dark blue
    secondary = Color(0xFF6750A4),          // Purple
    onSecondary = Color(0xFFFFFFFF),          // White
    secondaryContainer = Color(0xFFEADDFF),    // Light purple
    onSecondaryContainer = Color(0xFF4F378B),  // Dark purple
    tertiary = Color(0xFF00695C),            // Teal
    onTertiary = Color(0xFFFFFFFF),           // White
    tertiaryContainer = Color(0xFFA5D6A7),    // Light teal
    onTertiaryContainer = Color(0xFF004D40),  // Dark teal
    error = Color(0xFFBA1A1A),              // Red
    onError = Color(0xFFFFFFFF),              // White
    errorContainer = Color(0xFFFFDAD6),      // Light red
    onErrorContainer = Color(0xFF410002),    // Dark red
    background = Color(0xFFFFFBFE),            // Almost white
    onBackground = Color(0xFF1C1B1F),      // Dark gray
    surface = Color(0xFFFFFFFF),              // White
    onSurface = Color(0xFF1C1B1F),        // Dark gray
    outline = Color(0xFF79747E),            // Medium gray
    outlineVariant = Color(0xFFCAC4D0),      // Light gray
    scrim = Color(0xFF000000),              // Black
    inverseSurface = Color(0xFF313033),      // Dark surface
    inverseOnSurface = Color(0xFFF4EFF4),    // Light text
    inversePrimary = Color(0xFFC4C7F5)       // Light primary
)

val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),              // Light blue
    onPrimary = Color(0xFF1C1B1F),          // Dark gray
    primaryContainer = Color(0xFF004494),      // Dark blue
    onPrimaryContainer = Color(0xFFD1E4FF),    // Light blue
    secondary = Color(0xFF03DAC6),            // Light accent
    onSecondary = Color(0xFF313033),          // Dark gray
    secondaryContainer = Color(0xFF4F378B),      // Dark purple
    onSecondaryContainer = Color(0xFFEADDFF),    // Light purple
    tertiary = Color(0xFF03DAC6),            // Light accent
    onTertiary = Color(0xFF313033),          // Dark gray
    tertiaryContainer = Color(0xFF4F378B),      // Dark purple
    error = Color(0xFFFFB4AB),              // Light red
    onError = Color(0xFF690005),              // Dark red
    errorContainer = Color(0xFF93000A),      // Dark red
    background = Color(0xFF121212),            // Very dark
    onBackground = Color(0xFFECECEC),        // Light gray
    surface = Color(0xFF1E1E1E),            // Dark surface
    onSurface = Color(0xFFECECEC),          // Light gray
    outline = Color(0xFF938F99),            // Medium gray
    outlineVariant = Color(0xFF49454F),      // Dark gray
    scrim = Color(0xFF000000),              // Black
    inverseSurface = Color(0xFFE3F2FD),      // Light surface
    inverseOnSurface = Color(0xFF1C1B1F),      // Dark text
    inversePrimary = Color(0xFF1C1B1F)       // Dark primary
)

// Custom colors for better contrast
object CustomColors {
    // Light theme colors
    val LightTextPrimary = Color(0xFF1A1A1A)      // Almost black
    val LightTextSecondary = Color(0xFF666666)    // Medium gray
    val LightTextTertiary = Color(0xFF999999)    // Light gray
    val LightBackground = Color(0xFFFFFFFF)        // White
    val LightSurface = Color(0xFFF8F9FA)        // Very light gray
    val LightBorder = Color(0xFFE0E0E0)          // Light gray
    val LightCard = Color(0xFFFFFFFF)            // White
    val LightIcon = Color(0xFF1A1A1A)            // Dark gray
    
    // Dark theme colors
    val DarkTextPrimary = Color(0xFFFFFFFF)        // White
    val DarkTextSecondary = Color(0xFFB0B0B0)    // Light gray
    val DarkTextTertiary = Color(0xFF808080)    // Medium gray
    val DarkBackground = Color(0xFF121212)        // Very dark
    val DarkSurface = Color(0xFF1E1E1E)        // Dark surface
    val DarkBorder = Color(0xFF333333)          // Medium dark
    val DarkCard = Color(0xFF2A2A2A)            // Dark card
    val DarkIcon = Color(0xFFFFFFFF)              // White
    
    // Status colors (work for both themes)
    val Success = Color(0xFF4CAF50)              // Green
    val Warning = Color(0xFFFF9800)              // Orange
    val Error = Color(0xFFF44336)                // Red
    val Info = Color(0xFF2196F3)                // Blue
}

// Theme utilities
object ThemeUtils {
    fun getTextColor(isDark: Boolean): Color {
        return if (isDark) CustomColors.DarkTextPrimary else CustomColors.LightTextPrimary
    }
    
    fun getSecondaryTextColor(isDark: Boolean): Color {
        return if (isDark) CustomColors.DarkTextSecondary else CustomColors.LightTextSecondary
    }
    
    fun getBackgroundColor(isDark: Boolean): Color {
        return if (isDark) CustomColors.DarkBackground else CustomColors.LightBackground
    }
    
    fun getSurfaceColor(isDark: Boolean): Color {
        return if (isDark) CustomColors.DarkSurface else CustomColors.LightSurface
    }
    
    fun getBorderColor(isDark: Boolean): Color {
        return if (isDark) CustomColors.DarkBorder else CustomColors.LightBorder
    }
    
    fun getCardColor(isDark: Boolean): Color {
        return if (isDark) CustomColors.DarkCard else CustomColors.LightCard
    }
    
    fun getIconColor(isDark: Boolean): Color {
        return if (isDark) CustomColors.DarkIcon else CustomColors.LightIcon
    }
}
