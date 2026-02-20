package com.example.androidtemplate.features.billing

import com.example.androidtemplate.core.contracts.AndroidBillingContract
import com.google.common.truth.Truth.assertThat
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test

class BillingSyncRepositoryIntegrationTest {
  private lateinit var server: MockWebServer

  @Before
  fun setUp() {
    server = MockWebServer()
    server.start()
  }

  @After
  fun tearDown() {
    server.shutdown()
  }

  @Test
  fun syncPurchase_skipsNetworkWhenSyncDisabled() = runBlocking {
    val repository = BillingSyncRepository(
      baseUrl = server.url("/").toString(),
      bearerTokenProvider = { "token" },
      syncEnabled = false,
    )

    val result = repository.syncPurchase(
      purchase = StorePurchase(
        productId = "monthly",
        purchaseToken = "purchase-token",
        orderId = "order-1",
        purchaseState = StorePurchaseState.PURCHASED,
      ),
    )

    assertThat(result).isEqualTo(BillingSyncResult.Skipped)
    assertThat(server.requestCount).isEqualTo(0)
  }

  @Test
  fun syncPurchase_mapsPayloadAndPostsToImmutableEndpoint() = runBlocking {
    server.enqueue(MockResponse().setResponseCode(200))

    val repository = BillingSyncRepository(
      baseUrl = server.url("/").toString(),
      bearerTokenProvider = { "token-123" },
      syncEnabled = true,
    )

    val purchase = StorePurchase(
      productId = "annual",
      purchaseToken = "token-abc",
      orderId = "order-xyz",
      purchaseState = StorePurchaseState.PURCHASED,
      purchaseTime = "2026-02-20T00:00:00Z",
      acknowledged = true,
      autoRenewing = true,
      originalJson = "{\"ok\":true}",
      signature = "sig-1",
      packageName = "com.example.androidtemplate",
    )

    val result = repository.syncPurchase(purchase)

    assertThat(result).isEqualTo(BillingSyncResult.Success)

    val recorded = server.takeRequest(2, TimeUnit.SECONDS)
    assertThat(recorded).isNotNull()
    val nonNullRecorded = recorded!!
    assertThat(nonNullRecorded.path).isEqualTo(AndroidBillingContract.SYNC_ENDPOINT)
    assertThat(nonNullRecorded.getHeader("Authorization")).isEqualTo("Bearer token-123")

    val payload = Json.parseToJsonElement(nonNullRecorded.body.readUtf8()).jsonObject
    assertThat(payload["platform"]?.toString()).isEqualTo("\"android\"")
    assertThat(payload["storeProductId"]?.toString()).isEqualTo("\"annual\"")
    assertThat(payload["storeTransactionId"]?.toString()).isEqualTo("\"token-abc\"")
    assertThat(payload["externalOrderId"]?.toString()).isEqualTo("\"order-xyz\"")
    assertThat(payload["status"]?.toString()).isEqualTo("\"active\"")
  }
}
