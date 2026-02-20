package com.example.androidtemplate.features.billing

import com.example.androidtemplate.core.contracts.AndroidBillingContract

class PaywallFlowUseCase(
  private val billingStore: BillingStoreContract,
  private val syncService: BillingSyncContract,
) {
  var state: PaywallState = PaywallState.Idle
    private set

  suspend fun loadProducts(): PaywallState {
    state = PaywallState.LoadingProducts

    val products = billingStore.queryProducts(AndroidBillingContract.PRODUCT_IDS)
      .sortedBy { AndroidBillingContract.PRODUCT_IDS.indexOf(it.id) }

    state = PaywallState.Ready(products = products)
    return state
  }

  suspend fun purchase(productId: String): PaywallState {
    val currentReadyState = state as? PaywallState.Ready ?: return loadProductsThenPurchase(productId)

    state = PaywallState.Processing(operation = BillingOperation.Purchase(productId))

    val nextResult = when (val outcome = billingStore.purchase(productId)) {
      is StorePurchaseOutcome.Success -> {
        val syncStatus = when (syncService.syncPurchase(outcome.purchase)) {
          BillingSyncResult.Success,
          BillingSyncResult.Skipped -> PaywallSyncStatus.Synced
          is BillingSyncResult.Failure -> PaywallSyncStatus.SyncFailed
        }
        PaywallResult.Purchased(
          productId = outcome.purchase.productId,
          syncStatus = syncStatus,
        )
      }
      StorePurchaseOutcome.Cancelled -> PaywallResult.Cancelled
      StorePurchaseOutcome.Pending -> PaywallResult.Pending
      is StorePurchaseOutcome.Failed -> PaywallResult.Failed(outcome.message)
    }

    state = currentReadyState.copy(lastResult = nextResult)
    return state
  }

  suspend fun restorePurchases(): PaywallState {
    val currentReadyState = state as? PaywallState.Ready ?: return loadProductsThenRestore()

    state = PaywallState.Processing(operation = BillingOperation.Restore)

    val restored = billingStore.restore()
    val nextResult = if (restored.isEmpty()) {
      PaywallResult.Failed("No purchases found")
    } else {
      var hasSyncFailure = false
      restored.forEach { purchase ->
        when (syncService.syncPurchase(purchase)) {
          BillingSyncResult.Success,
          BillingSyncResult.Skipped -> Unit
          is BillingSyncResult.Failure -> hasSyncFailure = true
        }
      }
      PaywallResult.Restored(
        count = restored.size,
        syncStatus = if (hasSyncFailure) PaywallSyncStatus.SyncFailed else PaywallSyncStatus.Synced,
      )
    }

    state = currentReadyState.copy(lastResult = nextResult)
    return state
  }

  private suspend fun loadProductsThenPurchase(productId: String): PaywallState {
    loadProducts()
    return purchase(productId)
  }

  private suspend fun loadProductsThenRestore(): PaywallState {
    loadProducts()
    return restorePurchases()
  }
}

interface BillingStoreContract {
  suspend fun queryProducts(productIds: List<String>): List<BillingProduct>
  suspend fun purchase(productId: String): StorePurchaseOutcome
  suspend fun restore(): List<StorePurchase>
}

interface BillingSyncContract {
  suspend fun syncPurchase(purchase: StorePurchase): BillingSyncResult
}

data class BillingProduct(
  val id: String,
  val type: BillingProductType,
)

enum class BillingProductType {
  Subscription,
  OneTime,
}

sealed interface StorePurchaseOutcome {
  data class Success(val purchase: StorePurchase) : StorePurchaseOutcome
  data object Cancelled : StorePurchaseOutcome
  data object Pending : StorePurchaseOutcome
  data class Failed(val message: String) : StorePurchaseOutcome
}

sealed interface PaywallState {
  data object Idle : PaywallState
  data object LoadingProducts : PaywallState
  data class Processing(val operation: BillingOperation) : PaywallState
  data class Ready(
    val products: List<BillingProduct>,
    val lastResult: PaywallResult? = null,
  ) : PaywallState
}

sealed interface BillingOperation {
  data class Purchase(val productId: String) : BillingOperation
  data object Restore : BillingOperation
}

sealed interface PaywallResult {
  data class Purchased(
    val productId: String,
    val syncStatus: PaywallSyncStatus,
  ) : PaywallResult
  data class Restored(
    val count: Int,
    val syncStatus: PaywallSyncStatus,
  ) : PaywallResult
  data object Cancelled : PaywallResult
  data object Pending : PaywallResult
  data class Failed(val message: String) : PaywallResult
}

enum class PaywallSyncStatus {
  Synced,
  SyncFailed,
}
