package com.example.androidtemplate.core.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class DesignTokensTest {

  @Test
  fun colorTokens_matchSpecHexValues() {
    assertThat(DesignTokens.Color.Background).isEqualTo(0xFFFFFFFF)
    assertThat(DesignTokens.Color.Surface).isEqualTo(0xFFF5F5F5)
    assertThat(DesignTokens.Color.Border).isEqualTo(0xFFE0E0E0)
    assertThat(DesignTokens.Color.TextPrimary).isEqualTo(0xFF111111)
    assertThat(DesignTokens.Color.TextSecondary).isEqualTo(0xFF666666)
    assertThat(DesignTokens.Color.Primary).isEqualTo(0xFF000000)
    assertThat(DesignTokens.Color.OnPrimary).isEqualTo(0xFFFFFFFF)
  }

  @Test
  fun typographyScale_matchesSpec() {
    assertThat(DesignTokens.Typography.DisplaySp).isEqualTo(32)
    assertThat(DesignTokens.Typography.TitleSp).isEqualTo(22)
    assertThat(DesignTokens.Typography.BodySp).isEqualTo(16)
    assertThat(DesignTokens.Typography.CaptionSp).isEqualTo(13)
  }

  @Test
  fun spacingGrid_usesEightPointScale() {
    assertThat(DesignTokens.Spacing.Steps).containsExactly(4, 8, 12, 16, 24, 32, 40).inOrder()
    assertThat(DesignTokens.Spacing.BaseHorizontalDp).isEqualTo(20)
  }
}
