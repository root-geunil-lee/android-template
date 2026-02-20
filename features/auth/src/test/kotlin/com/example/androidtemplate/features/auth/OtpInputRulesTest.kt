package com.example.androidtemplate.features.auth

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class OtpInputRulesTest {

  @Test
  fun sanitizeOtpInput_keepsDigitsOnlyAndLimitsToSix() {
    val sanitized = sanitizeOtpInput("1a2b3c4d5e6f7")

    assertThat(sanitized).isEqualTo("123456")
  }

  @Test
  fun otpSlotDescription_exposesPositionalAccessibilityLabel() {
    assertThat(otpSlotDescription(0)).isEqualTo("digit 1 of 6")
    assertThat(otpSlotDescription(5)).isEqualTo("digit 6 of 6")
  }
}
