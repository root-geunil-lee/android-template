package com.example.androidtemplate.features.auth

const val OTP_CODE_LENGTH = 6

fun sanitizeOtpInput(rawInput: String): String {
  return rawInput.filter { it.isDigit() }.take(OTP_CODE_LENGTH)
}

fun otpSlotDescription(index: Int): String {
  val position = (index + 1).coerceIn(1, OTP_CODE_LENGTH)
  return "digit $position of $OTP_CODE_LENGTH"
}
