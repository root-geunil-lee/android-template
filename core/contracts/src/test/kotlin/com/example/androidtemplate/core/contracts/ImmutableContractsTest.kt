package com.example.androidtemplate.core.contracts

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ImmutableContractsTest {

  @Test
  fun productIds_areImmutable() {
    assertThat(AndroidBillingContract.PRODUCT_IDS)
      .containsExactly("monthly", "annual", "remove_ads", "lifetime")
      .inOrder()
  }

  @Test
  fun billingSyncPayloadFields_areImmutable() {
    assertThat(AndroidBillingContract.SYNC_PAYLOAD_FIELDS)
      .containsExactly(
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
      .inOrder()
  }

  @Test
  fun endpoints_areImmutable() {
    assertThat(AndroidAuthContract.OTP_ENDPOINT).isEqualTo("/auth/v1/otp")
    assertThat(AndroidAuthContract.VERIFY_ENDPOINT).isEqualTo("/auth/v1/verify")
    assertThat(AndroidAuthContract.LOGOUT_ENDPOINT).isEqualTo("/auth/v1/logout")

    assertThat(AndroidBillingContract.ENTITLEMENTS_ENDPOINT).isEqualTo("/api/v1/billing/entitlements/me")
    assertThat(AndroidBillingContract.PURCHASES_ENDPOINT).isEqualTo("/api/v1/billing/purchases/me?limit=50")
    assertThat(AndroidBillingContract.SYNC_ENDPOINT).isEqualTo("/api/v1/billing/purchases/sync")
  }
}
