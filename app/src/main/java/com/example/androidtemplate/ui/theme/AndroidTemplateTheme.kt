package com.example.androidtemplate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
  background = Color(0xFFFFFFFF),
  surface = Color(0xFFF5F5F5),
  outline = Color(0xFFE0E0E0),
  onBackground = Color(0xFF111111),
  onSurface = Color(0xFF111111),
  onSurfaceVariant = Color(0xFF666666),
  primary = Color(0xFF000000),
  onPrimary = Color(0xFFFFFFFF),
)

private val DarkColorScheme = darkColorScheme(
  background = Color(0xFF111111),
  surface = Color(0xFF1C1C1C),
  outline = Color(0xFF303030),
  onBackground = Color(0xFFF5F5F5),
  onSurface = Color(0xFFF5F5F5),
  onSurfaceVariant = Color(0xFFCCCCCC),
  primary = Color(0xFFFFFFFF),
  onPrimary = Color(0xFF111111),
)

@Composable
fun AndroidTemplateTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    content = content,
  )
}
