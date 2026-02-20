package com.example.androidtemplate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtemplate.core.ui.DesignTokens

private data class MonochromeColorRoles(
  val primary: Color,
  val onPrimary: Color,
  val surface: Color,
  val surfaceVariant: Color,
  val outline: Color,
  val onSurface: Color,
  val onSurfaceVariant: Color,
)

private val MonochromeRoles = MonochromeColorRoles(
  primary = Color(0xFF111111),
  onPrimary = Color(0xFFFFFFFF),
  surface = Color(0xFFFFFFFF),
  surfaceVariant = Color(0xFFF5F5F5),
  outline = Color(0xFFE0E0E0),
  onSurface = Color(0xFF111111),
  onSurfaceVariant = Color(0xFF666666),
)

private val MonochromeColorScheme = lightColorScheme(
  primary = MonochromeRoles.primary,
  onPrimary = MonochromeRoles.onPrimary,
  background = MonochromeRoles.surface,
  onBackground = MonochromeRoles.onSurface,
  surface = MonochromeRoles.surface,
  onSurface = MonochromeRoles.onSurface,
  surfaceVariant = MonochromeRoles.surfaceVariant,
  onSurfaceVariant = MonochromeRoles.onSurfaceVariant,
  outline = MonochromeRoles.outline,
  primaryContainer = MonochromeRoles.surfaceVariant,
  onPrimaryContainer = MonochromeRoles.onSurface,
)

private val SharedTypography = Typography(
  displayLarge = TextStyle(fontSize = DesignTokens.Typography.DisplaySp.sp),
  headlineMedium = TextStyle(fontSize = DesignTokens.Typography.TitleSp.sp),
  titleLarge = TextStyle(fontSize = DesignTokens.Typography.TitleSp.sp),
  bodyLarge = TextStyle(fontSize = DesignTokens.Typography.BodySp.sp),
  bodyMedium = TextStyle(fontSize = DesignTokens.Typography.BodySp.sp),
  labelSmall = TextStyle(fontSize = DesignTokens.Typography.CaptionSp.sp),
)

private val SharedShapes = Shapes(
  small = RoundedCornerShape(DesignTokens.Shape.ContainerDefaultDp.dp),
  medium = RoundedCornerShape(DesignTokens.Shape.ContainerDefaultDp.dp),
  large = RoundedCornerShape(DesignTokens.Shape.PrimaryCtaDp.dp),
)

@Composable
fun AndroidTemplateTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  // Template policy: fixed neutral monochrome palette (no dynamic/system accent colors).
  val resolvedColorScheme = if (darkTheme) MonochromeColorScheme else MonochromeColorScheme
  MaterialTheme(
    colorScheme = resolvedColorScheme,
    typography = SharedTypography,
    shapes = SharedShapes,
    content = content,
  )
}
