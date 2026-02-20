package com.example.androidtemplate.core.contracts

object AndroidAuthContract {
  const val OTP_ENDPOINT = "/auth/v1/otp"
  const val VERIFY_ENDPOINT = "/auth/v1/verify"
  const val LOGOUT_ENDPOINT = "/auth/v1/logout"
}

object AndroidBillingContract {
  val PRODUCT_IDS = listOf("monthly", "annual", "remove_ads", "lifetime")

  const val ENTITLEMENTS_ENDPOINT = "/api/v1/billing/entitlements/me"
  const val PURCHASES_ENDPOINT = "/api/v1/billing/purchases/me?limit=50"
  const val SYNC_ENDPOINT = "/api/v1/billing/purchases/sync"

  val SYNC_PAYLOAD_FIELDS = listOf(
    "platform",
    "storeProductId",
    "storeTransactionId",
    "externalOrderId",
    "status",
    "purchasedAt",
    "expiresAt",
    "canceledAt",
    "refundedAt",
    "raw",
  )
}
