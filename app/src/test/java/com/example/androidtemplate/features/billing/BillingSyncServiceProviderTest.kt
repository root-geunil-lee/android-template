package com.example.androidtemplate.features.billing

import org.junit.Assert.assertTrue
import org.junit.Test

class BillingSyncServiceProviderTest {

  @Test
  fun createBillingSyncService_returnsNoopWhenDisabled() {
    val service = createBillingSyncService(
      apiBaseUrl = "https://api.example.com",
      syncEnabled = false,
      bearerToken = "token-1",
    )

    assertTrue(service === NoopBillingSyncService)
  }

  @Test
  fun createBillingSyncService_returnsRepositoryWhenEnabledAndBaseUrlPresent() {
    val service = createBillingSyncService(
      apiBaseUrl = "https://api.example.com",
      syncEnabled = true,
      bearerToken = "token-1",
    )

    assertTrue(service is BillingSyncRepository)
  }
}
