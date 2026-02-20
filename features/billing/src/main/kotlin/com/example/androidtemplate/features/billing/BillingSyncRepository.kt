package com.example.androidtemplate.features.billing

import com.example.androidtemplate.core.contracts.AndroidBillingContract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class BillingSyncRepository(
  private val baseUrl: String,
  private val bearerTokenProvider: () -> String?,
  private val syncEnabled: Boolean,
  private val httpClient: OkHttpClient = OkHttpClient(),
  private val json: Json = Json { encodeDefaults = true },
) : BillingSyncContract {
  override
  suspend fun syncPurchase(purchase: StorePurchase): BillingSyncResult {
    if (!syncEnabled) {
      return BillingSyncResult.Skipped
    }

    val payload = BillingSyncPayload(
      platform = "android",
      storeProductId = purchase.productId,
      storeTransactionId = purchase.purchaseToken,
      externalOrderId = purchase.orderId,
      status = purchase.purchaseState.toSyncStatus(),
      purchasedAt = purchase.purchaseTime,
      expiresAt = purchase.expiresAt,
      canceledAt = purchase.canceledAt,
      refundedAt = purchase.refundedAt,
      raw = BillingSyncRaw(
        purchaseToken = purchase.purchaseToken,
        orderId = purchase.orderId,
        packageName = purchase.packageName,
        purchaseTime = purchase.purchaseTime,
        purchaseState = purchase.purchaseState.name,
        acknowledged = purchase.acknowledged,
        autoRenewing = purchase.autoRenewing,
        originalJson = purchase.originalJson,
        signature = purchase.signature,
      ),
    )

    val requestBuilder = Request.Builder()
      .url(baseUrl.trimEnd('/') + AndroidBillingContract.SYNC_ENDPOINT)
      .post(json.encodeToString(payload).toRequestBody("application/json".toMediaType()))

    val token = bearerTokenProvider()
    if (!token.isNullOrBlank()) {
      requestBuilder.header("Authorization", "Bearer $token")
    }

    val responseCode = withContext(Dispatchers.IO) {
      httpClient.newCall(requestBuilder.build()).execute().use { response ->
        response.code
      }
    }

    return if (responseCode in 200..299) {
      BillingSyncResult.Success
    } else {
      BillingSyncResult.Failure(responseCode)
    }
  }
}

private fun StorePurchaseState.toSyncStatus(): String = when (this) {
  StorePurchaseState.PURCHASED -> "active"
  StorePurchaseState.PENDING -> "pending"
  StorePurchaseState.USER_CANCELED -> "canceled"
  StorePurchaseState.FAILED -> "failed"
  StorePurchaseState.REFUNDED -> "refunded"
  StorePurchaseState.EXPIRED -> "expired"
}

sealed interface BillingSyncResult {
  data object Skipped : BillingSyncResult
  data object Success : BillingSyncResult
  data class Failure(val code: Int) : BillingSyncResult
}

enum class StorePurchaseState {
  PURCHASED,
  PENDING,
  USER_CANCELED,
  FAILED,
  REFUNDED,
  EXPIRED,
}

data class StorePurchase(
  val productId: String,
  val purchaseToken: String,
  val orderId: String?,
  val purchaseState: StorePurchaseState,
  val purchaseTime: String? = null,
  val expiresAt: String? = null,
  val canceledAt: String? = null,
  val refundedAt: String? = null,
  val packageName: String? = null,
  val acknowledged: Boolean = false,
  val autoRenewing: Boolean = false,
  val originalJson: String? = null,
  val signature: String? = null,
)

@Serializable
private data class BillingSyncPayload(
  val platform: String,
  val storeProductId: String,
  val storeTransactionId: String,
  val externalOrderId: String?,
  val status: String,
  val purchasedAt: String?,
  val expiresAt: String?,
  val canceledAt: String?,
  val refundedAt: String?,
  val raw: BillingSyncRaw,
)

@Serializable
private data class BillingSyncRaw(
  val purchaseToken: String,
  val orderId: String?,
  val packageName: String?,
  val purchaseTime: String?,
  val purchaseState: String,
  val acknowledged: Boolean,
  val autoRenewing: Boolean,
  val originalJson: String?,
  val signature: String?,
)
