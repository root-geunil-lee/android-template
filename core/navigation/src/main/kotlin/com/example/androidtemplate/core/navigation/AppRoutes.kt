package com.example.androidtemplate.core.navigation

object AppRoutes {
  const val AUTH_METHODS = "auth/methods"
  const val AUTH_EMAIL = "auth/email"
  const val AUTH_OTP = "auth/otp?email={email}"
  const val HOME = "home"
  const val MYPAGE = "mypage"
  const val MYPAGE_SUBSCRIPTION = "mypage/subscription"
  const val MYPAGE_PURCHASE_HISTORY = "mypage/purchase-history"
  const val MYPAGE_TRANSACTION_DETAIL = "mypage/transaction/{id}"
  const val PAYWALL = "paywall"
}
