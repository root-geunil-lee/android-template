package com.example.androidtemplate.core.ui

fun effectiveHorizontalPaddingPx(
  baselinePx: Int,
  safeInsetLeftPx: Int,
  safeInsetRightPx: Int,
): Int = maxOf(baselinePx, safeInsetLeftPx, safeInsetRightPx)
