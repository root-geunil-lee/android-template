package com.example.androidtemplate.features.billing

import com.example.androidtemplate.BuildConfig

fun provideBillingSyncService(): BillingSyncContract {
  return createBillingSyncService(
    apiBaseUrl = BuildConfig.API_BASE_URL,
    syncEnabled = BuildConfig.BILLING_SYNC_ENABLED,
    bearerToken = BuildConfig.BILLING_BEARER_TOKEN,
  )
}

internal fun createBillingSyncService(
  apiBaseUrl: String,
  syncEnabled: Boolean,
  bearerToken: String?,
): BillingSyncContract {
  if (!syncEnabled || apiBaseUrl.isBlank()) {
    return NoopBillingSyncService
  }

  return BillingSyncRepository(
    baseUrl = apiBaseUrl,
    bearerTokenProvider = {
      bearerToken?.takeIf { it.isNotBlank() }
    },
    syncEnabled = true,
  )
}

object NoopBillingSyncService : BillingSyncContract {
  override suspend fun syncPurchase(purchase: StorePurchase): BillingSyncResult = BillingSyncResult.Skipped
}
