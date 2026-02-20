package com.example.androidtemplate.features.billing

import com.example.androidtemplate.core.ui.UiEvent
import com.example.androidtemplate.core.ui.UiState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test

class PaywallUiStoreTest {

  @Test
  fun load_setsSuccessUiStateWithProducts() = runBlocking {
    val uiStore = PaywallUiStore(
      useCase = PaywallFlowUseCase(
        billingStore = FakeBillingStore(),
        syncService = FakeBillingSyncService(),
      ),
    )

    uiStore.load()

    assertThat(uiStore.state).isInstanceOf(UiState.Success::class.java)
    val success = uiStore.state as UiState.Success<PaywallUiModel>
    assertThat(success.data.products.map { it.id })
      .containsExactly("monthly", "annual", "remove_ads", "lifetime")
      .inOrder()
  }

  @Test
  fun purchase_success_emitsDismissSheetEvent() = runBlocking {
    val uiStore = PaywallUiStore(
      useCase = PaywallFlowUseCase(
        billingStore = FakeBillingStore(
          purchaseOutcome = StorePurchaseOutcome.Success(
            StorePurchase(
              productId = "monthly",
              purchaseToken = "token-1",
              orderId = "order-1",
              purchaseState = StorePurchaseState.PURCHASED,
            ),
          ),
        ),
        syncService = FakeBillingSyncService(),
      ),
    )
    uiStore.load()

    uiStore.purchase("monthly")
    val event = withTimeout(1000) { uiStore.events.first() }

    assertThat(event).isEqualTo(UiEvent.DismissSheet(PAYWALL_SHEET_ID))
  }

  @Test
  fun purchase_cancelled_emitsSnackbarEvent() = runBlocking {
    val uiStore = PaywallUiStore(
      useCase = PaywallFlowUseCase(
        billingStore = FakeBillingStore(
          purchaseOutcome = StorePurchaseOutcome.Cancelled,
        ),
        syncService = FakeBillingSyncService(),
      ),
    )
    uiStore.load()

    uiStore.purchase("monthly")
    val event = withTimeout(1000) { uiStore.events.first() }

    assertThat(event).isEqualTo(UiEvent.ShowSnackbar("Purchase cancelled"))
  }

  private class FakeBillingSyncService(
    private val syncResult: BillingSyncResult = BillingSyncResult.Success,
  ) : BillingSyncContract {
    override suspend fun syncPurchase(purchase: StorePurchase): BillingSyncResult = syncResult
  }

  private class FakeBillingStore(
    private val products: List<BillingProduct> = listOf(
      BillingProduct("monthly", BillingProductType.Subscription),
      BillingProduct("annual", BillingProductType.Subscription),
      BillingProduct("remove_ads", BillingProductType.OneTime),
      BillingProduct("lifetime", BillingProductType.OneTime),
    ),
    private val purchaseOutcome: StorePurchaseOutcome = StorePurchaseOutcome.Success(
      StorePurchase(
        productId = "monthly",
        purchaseToken = "token-default",
        orderId = "order-default",
        purchaseState = StorePurchaseState.PURCHASED,
      ),
    ),
  ) : BillingStoreContract {
    override suspend fun queryProducts(productIds: List<String>): List<BillingProduct> = products
    override suspend fun purchase(productId: String): StorePurchaseOutcome = purchaseOutcome
    override suspend fun restore(): List<StorePurchase> = emptyList()
  }
}
