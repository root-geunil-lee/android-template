package com.example.androidtemplate.core.ui

object DesignTokens {
  object Color {
    const val Background: Long = 0xFFFFFFFF
    const val Surface: Long = 0xFFF5F5F5
    const val Border: Long = 0xFFE0E0E0
    const val TextPrimary: Long = 0xFF111111
    const val TextSecondary: Long = 0xFF666666
    const val Divider: Long = 0xFFEEEEEE
    const val Primary: Long = 0xFF000000
    const val OnPrimary: Long = 0xFFFFFFFF
  }

  object Typography {
    const val DisplaySp: Int = 32
    const val TitleSp: Int = 22
    const val BodySp: Int = 16
    const val CaptionSp: Int = 13
  }

  object Shape {
    const val ContainerDefaultDp: Int = 12
    const val PrimaryCtaDp: Int = 16
    const val OtpMinDp: Int = 12
    const val OtpMaxDp: Int = 14
  }

  object Spacing {
    val Steps = listOf(4, 8, 12, 16, 24, 32, 40)
    const val BaseHorizontalDp: Int = 20
  }
}
