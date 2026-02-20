package com.example.androidtemplate.features.billing

import com.example.androidtemplate.core.contracts.AndroidBillingContract
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.runBlocking
import org.junit.Test

class PaywallFlowUseCaseTest {

  @Test
  fun loadProducts_queriesImmutableProductIdsAndExposesReadyState() = runBlocking {
    val fakeStore = FakeBillingStore()
    val useCase = PaywallFlowUseCase(
      billingStore = fakeStore,
      syncService = FakeBillingSyncService(),
    )

    val state = useCase.loadProducts()

    assertThat(fakeStore.lastQueriedProductIds).containsExactlyElementsIn(AndroidBillingContract.PRODUCT_IDS).inOrder()
    assertThat(state).isInstanceOf(PaywallState.Ready::class.java)
    val ready = state as PaywallState.Ready
    assertThat(ready.products.map { it.id }).containsExactlyElementsIn(AndroidBillingContract.PRODUCT_IDS).inOrder()
  }

  @Test
  fun purchase_cancelled_mapsToCancelledResult() = runBlocking {
    val fakeStore = FakeBillingStore(purchaseOutcome = StorePurchaseOutcome.Cancelled)
    val useCase = PaywallFlowUseCase(
      billingStore = fakeStore,
      syncService = FakeBillingSyncService(),
    )

    useCase.loadProducts()
    val state = useCase.purchase("monthly")

    val ready = state as PaywallState.Ready
    assertThat(ready.lastResult).isEqualTo(PaywallResult.Cancelled)
  }

  @Test
  fun purchase_pending_mapsToPendingResult() = runBlocking {
    val fakeStore = FakeBillingStore(purchaseOutcome = StorePurchaseOutcome.Pending)
    val useCase = PaywallFlowUseCase(
      billingStore = fakeStore,
      syncService = FakeBillingSyncService(),
    )

    useCase.loadProducts()
    val state = useCase.purchase("monthly")

    val ready = state as PaywallState.Ready
    assertThat(ready.lastResult).isEqualTo(PaywallResult.Pending)
  }

  @Test
  fun purchase_success_syncsPurchaseAndMapsToPurchased() = runBlocking {
    val fakeSync = FakeBillingSyncService()
    val fakeStore = FakeBillingStore(
      purchaseOutcome = StorePurchaseOutcome.Success(
        StorePurchase(
          productId = "monthly",
          purchaseToken = "token-1",
          orderId = "order-1",
          purchaseState = StorePurchaseState.PURCHASED,
        ),
      ),
    )

    val useCase = PaywallFlowUseCase(
      billingStore = fakeStore,
      syncService = fakeSync,
    )

    useCase.loadProducts()
    val state = useCase.purchase("monthly")

    assertThat(fakeSync.syncedPurchasesCount).isEqualTo(1)
    val ready = state as PaywallState.Ready
    assertThat(ready.lastResult).isEqualTo(PaywallResult.Purchased(productId = "monthly"))
  }

  @Test
  fun restore_syncsAllPurchasesAndMapsToRestored() = runBlocking {
    val fakeSync = FakeBillingSyncService()
    val fakeStore = FakeBillingStore(
      restorePurchases = listOf(
        StorePurchase(
          productId = "monthly",
          purchaseToken = "token-1",
          orderId = "order-1",
          purchaseState = StorePurchaseState.PURCHASED,
        ),
        StorePurchase(
          productId = "lifetime",
          purchaseToken = "token-2",
          orderId = "order-2",
          purchaseState = StorePurchaseState.PURCHASED,
        ),
      ),
    )

    val useCase = PaywallFlowUseCase(
      billingStore = fakeStore,
      syncService = fakeSync,
    )

    useCase.loadProducts()
    val state = useCase.restorePurchases()

    assertThat(fakeSync.syncedPurchasesCount).isEqualTo(2)
    val ready = state as PaywallState.Ready
    assertThat(ready.lastResult).isEqualTo(PaywallResult.Restored(count = 2))
  }

  private class FakeBillingSyncService : BillingSyncContract {
    var syncedPurchasesCount: Int = 0

    override suspend fun syncPurchase(purchase: StorePurchase): BillingSyncResult {
      syncedPurchasesCount += 1
      return BillingSyncResult.Success
    }
  }

  private class FakeBillingStore(
    private val products: List<BillingProduct> = AndroidBillingContract.PRODUCT_IDS.map {
      BillingProduct(id = it, type = if (it == "monthly" || it == "annual") BillingProductType.Subscription else BillingProductType.OneTime)
    },
    private val purchaseOutcome: StorePurchaseOutcome = StorePurchaseOutcome.Success(
      StorePurchase(
        productId = "monthly",
        purchaseToken = "token-default",
        orderId = "order-default",
        purchaseState = StorePurchaseState.PURCHASED,
      ),
    ),
    private val restorePurchases: List<StorePurchase> = emptyList(),
  ) : BillingStoreContract {
    var lastQueriedProductIds: List<String> = emptyList()

    override suspend fun queryProducts(productIds: List<String>): List<BillingProduct> {
      lastQueriedProductIds = productIds
      return products
    }

    override suspend fun purchase(productId: String): StorePurchaseOutcome = purchaseOutcome

    override suspend fun restore(): List<StorePurchase> = restorePurchases
  }
}
