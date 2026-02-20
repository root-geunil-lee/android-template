package com.example.androidtemplate.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtemplate.core.ui.DesignTokens

private data class BrandColorRoles(
  val background: Color,
  val surface: Color,
  val surfaceVariant: Color,
  val outline: Color,
  val primary: Color,
  val onPrimary: Color,
  val primaryContainer: Color,
  val onPrimaryContainer: Color,
  val onSurface: Color,
  val onSurfaceVariant: Color,
)

private fun brandColorRoles(darkTheme: Boolean): BrandColorRoles = if (darkTheme) {
  BrandColorRoles(
    background = Color(DesignTokens.Color.TextPrimary),
    surface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFF262626),
    outline = Color(0xFF3A3A3A),
    primary = Color(DesignTokens.Color.OnPrimary),
    onPrimary = Color(DesignTokens.Color.TextPrimary),
    primaryContainer = Color(0xFF2B2B2B),
    onPrimaryContainer = Color(0xFFEAEAEA),
    onSurface = Color(DesignTokens.Color.Surface),
    onSurfaceVariant = Color(0xFFCCCCCC),
  )
} else {
  BrandColorRoles(
    background = Color(DesignTokens.Color.Background),
    surface = Color(DesignTokens.Color.Surface),
    surfaceVariant = Color(DesignTokens.Color.Surface),
    outline = Color(DesignTokens.Color.Border),
    primary = Color(DesignTokens.Color.Primary),
    onPrimary = Color(DesignTokens.Color.OnPrimary),
    primaryContainer = Color(DesignTokens.Color.Surface),
    onPrimaryContainer = Color(DesignTokens.Color.TextPrimary),
    onSurface = Color(DesignTokens.Color.TextPrimary),
    onSurfaceVariant = Color(DesignTokens.Color.TextSecondary),
  )
}

private fun lightBrandFallbackColorScheme(brand: BrandColorRoles): ColorScheme = lightColorScheme(
  background = brand.background,
  surface = brand.surface,
  surfaceVariant = brand.surfaceVariant,
  outline = brand.outline,
  onBackground = brand.onSurface,
  onSurface = brand.onSurface,
  onSurfaceVariant = brand.onSurfaceVariant,
  primary = brand.primary,
  onPrimary = brand.onPrimary,
  primaryContainer = brand.primaryContainer,
  onPrimaryContainer = brand.onPrimaryContainer,
)

private fun darkBrandFallbackColorScheme(brand: BrandColorRoles): ColorScheme = darkColorScheme(
  background = brand.background,
  surface = brand.surface,
  surfaceVariant = brand.surfaceVariant,
  outline = brand.outline,
  onBackground = brand.onSurface,
  onSurface = brand.onSurface,
  onSurfaceVariant = brand.onSurfaceVariant,
  primary = brand.primary,
  onPrimary = brand.onPrimary,
  primaryContainer = brand.primaryContainer,
  onPrimaryContainer = brand.onPrimaryContainer,
)

private fun ColorScheme.withBrandLayer(brand: BrandColorRoles): ColorScheme = copy(
  primary = lerp(primary, brand.primary, 0.18f),
  onPrimary = lerp(onPrimary, brand.onPrimary, 0.08f),
  primaryContainer = lerp(primaryContainer, brand.primaryContainer, 0.24f),
  onPrimaryContainer = lerp(onPrimaryContainer, brand.onPrimaryContainer, 0.14f),
  surface = lerp(surface, brand.surface, 0.28f),
  surfaceVariant = lerp(surfaceVariant, brand.surfaceVariant, 0.35f),
  outline = lerp(outline, brand.outline, 0.45f),
  onSurface = lerp(onSurface, brand.onSurface, 0.22f),
  onSurfaceVariant = lerp(onSurfaceVariant, brand.onSurfaceVariant, 0.28f),
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
  dynamicColor: Boolean = true,
  content: @Composable () -> Unit,
) {
  val context = LocalContext.current
  val brandRoles = brandColorRoles(darkTheme)
  val baseColorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> darkBrandFallbackColorScheme(brandRoles)
    else -> lightBrandFallbackColorScheme(brandRoles)
  }
  val resolvedColorScheme = if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    baseColorScheme.withBrandLayer(brandRoles)
  } else {
    baseColorScheme
  }

  MaterialTheme(
    colorScheme = resolvedColorScheme,
    typography = SharedTypography,
    shapes = SharedShapes,
    content = content,
  )
}
