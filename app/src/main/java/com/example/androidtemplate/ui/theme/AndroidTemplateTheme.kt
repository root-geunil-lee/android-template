package com.example.androidtemplate.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtemplate.core.ui.DesignTokens

private val LightColorScheme = lightColorScheme(
  background = Color(DesignTokens.Color.Background),
  surface = Color(DesignTokens.Color.Surface),
  outline = Color(DesignTokens.Color.Border),
  onBackground = Color(DesignTokens.Color.TextPrimary),
  onSurface = Color(DesignTokens.Color.TextPrimary),
  onSurfaceVariant = Color(DesignTokens.Color.TextSecondary),
  primary = Color(DesignTokens.Color.Primary),
  onPrimary = Color(DesignTokens.Color.OnPrimary),
)

private val DarkColorScheme = darkColorScheme(
  background = Color(DesignTokens.Color.TextPrimary),
  surface = Color(0xFF1C1C1C),
  outline = Color(0xFF303030),
  onBackground = Color(DesignTokens.Color.Surface),
  onSurface = Color(DesignTokens.Color.Surface),
  onSurfaceVariant = Color(0xFFCCCCCC),
  primary = Color(DesignTokens.Color.OnPrimary),
  onPrimary = Color(DesignTokens.Color.TextPrimary),
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
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    typography = SharedTypography,
    shapes = SharedShapes,
    content = content,
  )
}
