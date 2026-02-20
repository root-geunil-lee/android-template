package com.example.androidtemplate.core.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class InsetPaddingPolicyTest {

  @Test
  fun effectiveHorizontalPaddingPx_usesBaselineWhenInsetsAreSmaller() {
    val px = effectiveHorizontalPaddingPx(
      baselinePx = 20,
      safeInsetLeftPx = 4,
      safeInsetRightPx = 12,
    )

    assertThat(px).isEqualTo(20)
  }

  @Test
  fun effectiveHorizontalPaddingPx_usesLargestSafeInset() {
    val px = effectiveHorizontalPaddingPx(
      baselinePx = 20,
      safeInsetLeftPx = 32,
      safeInsetRightPx = 24,
    )

    assertThat(px).isEqualTo(32)
  }
}
