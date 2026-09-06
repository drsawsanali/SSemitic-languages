package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.ui.MainApp
import com.example.ui.theme.AppThemeMode
import com.example.ui.theme.SemiticEncyclopediaTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      var currentThemeMode by remember { mutableStateOf(AppThemeMode.SEPIA) }

      SemiticEncyclopediaTheme(themeMode = currentThemeMode) {
        MainApp(
          onToggleTheme = {
            currentThemeMode = when (currentThemeMode) {
              AppThemeMode.SEPIA -> AppThemeMode.DARK
              AppThemeMode.DARK -> AppThemeMode.LIGHT
              AppThemeMode.LIGHT -> AppThemeMode.SEPIA
            }
          }
        )
      }
    }
  }
}
