package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

enum class AppThemeMode {
  SEPIA,
  DARK,
  LIGHT
}

data class CustomColors(
  val cardBackground: Color,
  val cardElevated: Color,
  val mutedText: Color,
  val border: Color,
  val gold: Color,
  val good: Color,
  val bad: Color,
  val bgSecondary: Color,
  val terminalBg: Color = TerminalBg,
  val terminalText: Color = TerminalText,
)

val LocalCustomColors = staticCompositionLocalOf {
  CustomColors(
    cardBackground = SepiaCard,
    cardElevated = SepiaCardElevated,
    mutedText = SepiaMuted,
    border = SepiaBorder,
    gold = SepiaGold,
    good = SepiaGood,
    bad = SepiaBad,
    bgSecondary = SepiaBgSecondary
  )
}

val SepiaColorScheme = lightColorScheme(
  primary = SepiaPrimary,
  onPrimary = Color.White,
  primaryContainer = SepiaPrimaryVariant,
  onPrimaryContainer = Color.White,
  secondary = SepiaAccent,
  onSecondary = Color.White,
  tertiary = SepiaGold,
  onTertiary = Color.White,
  background = SepiaBg,
  onBackground = SepiaText,
  surface = SepiaCard,
  onSurface = SepiaText,
  surfaceVariant = SepiaBgSecondary,
  onSurfaceVariant = SepiaMuted,
  outline = SepiaBorder,
  error = SepiaBad,
  onError = Color.White
)

val DarkAppColorScheme = darkColorScheme(
  primary = DarkPrimary,
  onPrimary = Color(0xFF2B1705),
  primaryContainer = DarkPrimaryVariant,
  onPrimaryContainer = Color(0xFF1E1003),
  secondary = DarkAccent,
  onSecondary = Color(0xFF0F1E33),
  tertiary = DarkGold,
  onTertiary = Color(0xFF261800),
  background = DarkBg,
  onBackground = DarkText,
  surface = DarkCard,
  onSurface = DarkText,
  surfaceVariant = DarkBgSecondary,
  onSurfaceVariant = DarkMuted,
  outline = DarkBorder,
  error = DarkBad,
  onError = Color.Black
)

val LightAppColorScheme = lightColorScheme(
  primary = LightPrimary,
  onPrimary = Color.White,
  primaryContainer = LightPrimaryVariant,
  onPrimaryContainer = Color.White,
  secondary = LightAccent,
  onSecondary = Color.White,
  tertiary = LightGold,
  onTertiary = Color.White,
  background = LightBg,
  onBackground = LightText,
  surface = LightCard,
  onSurface = LightText,
  surfaceVariant = LightBgSecondary,
  onSurfaceVariant = LightMuted,
  outline = LightBorder,
  error = LightBad,
  onError = Color.White
)

@Composable
fun SemiticEncyclopediaTheme(
  themeMode: AppThemeMode = AppThemeMode.SEPIA,
  content: @Composable () -> Unit
) {
  val (colorScheme, customColors) = when (themeMode) {
    AppThemeMode.SEPIA -> Pair(
      SepiaColorScheme,
      CustomColors(
        cardBackground = SepiaCard,
        cardElevated = SepiaCardElevated,
        mutedText = SepiaMuted,
        border = SepiaBorder,
        gold = SepiaGold,
        good = SepiaGood,
        bad = SepiaBad,
        bgSecondary = SepiaBgSecondary
      )
    )
    AppThemeMode.DARK -> Pair(
      DarkAppColorScheme,
      CustomColors(
        cardBackground = DarkCard,
        cardElevated = DarkCardElevated,
        mutedText = DarkMuted,
        border = DarkBorder,
        gold = DarkGold,
        good = DarkGood,
        bad = DarkBad,
        bgSecondary = DarkBgSecondary
      )
    )
    AppThemeMode.LIGHT -> Pair(
      LightAppColorScheme,
      CustomColors(
        cardBackground = LightCard,
        cardElevated = LightCardElevated,
        mutedText = LightMuted,
        border = LightBorder,
        gold = LightGold,
        good = LightGood,
        bad = LightBad,
        bgSecondary = LightBgSecondary
      )
    )
  }

  CompositionLocalProvider(LocalCustomColors provides customColors) {
    MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
    )
  }
}
