package com.example.androidtemplate.features.billing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidtemplate.core.contracts.AndroidBillingContract
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallSheetRoute(
  onClose: () -> Unit,
  onResult: (PaywallResult) -> Unit,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val coroutineScope = rememberCoroutineScope()
  val useCase = remember {
    PaywallFlowUseCase(
      billingStore = DemoBillingStore(),
      syncService = NoopBillingSyncService,
    )
  }

  var state by remember { mutableStateOf<PaywallState>(PaywallState.Idle) }
  var cachedProducts by remember { mutableStateOf(emptyList<BillingProduct>()) }

  LaunchedEffect(Unit) {
    state = useCase.loadProducts()
  }

  if (state is PaywallState.Ready) {
    cachedProducts = (state as PaywallState.Ready).products
  }

  val products = if (state is PaywallState.Ready) {
    (state as PaywallState.Ready).products
  } else {
    cachedProducts
  }
  val isProcessing = state is PaywallState.LoadingProducts || state is PaywallState.Processing
  val resultMessage = (state as? PaywallState.Ready)?.lastResult?.toUserMessage()

  BackHandler {
    onResult(PaywallResult.Cancelled)
    onClose()
  }

  ModalBottomSheet(
    onDismissRequest = {
      onResult(PaywallResult.Cancelled)
      onClose()
    },
    sheetState = sheetState,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.navigationBars)
        .padding(20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Text("Choose your plan", style = MaterialTheme.typography.titleLarge)

      if (products.isEmpty()) {
        CircularProgressIndicator()
      }

      products.forEach { product ->
        Button(
          onClick = {
            coroutineScope.launch {
              state = useCase.purchase(product.id)
              val result = (state as? PaywallState.Ready)?.lastResult
              if (result is PaywallResult.Purchased || result is PaywallResult.Restored) {
                onResult(result)
                onClose()
              }
            }
          },
          enabled = !isProcessing,
          modifier = Modifier.fillMaxWidth(),
        ) {
          Text(planLabel(product.id))
        }
      }

      Button(
        onClick = {
          coroutineScope.launch {
            state = useCase.restorePurchases()
            val result = (state as? PaywallState.Ready)?.lastResult
            if (result is PaywallResult.Restored) {
              onResult(result)
              onClose()
            }
          }
        },
        enabled = !isProcessing,
        modifier = Modifier.fillMaxWidth(),
      ) {
        Text("Restore purchases")
      }

      if (!resultMessage.isNullOrBlank()) {
        Spacer(Modifier.height(4.dp))
        Text(
          text = resultMessage,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
      }

      Spacer(Modifier.windowInsetsBottomHeight(androidx.compose.foundation.layout.WindowInsets.navigationBars))
    }
  }
}

private fun planLabel(productId: String): String = when (productId) {
  "monthly" -> "Start monthly"
  "annual" -> "Start annual"
  "remove_ads" -> "Remove ads"
  "lifetime" -> "Buy lifetime"
  else -> productId
}

private fun PaywallResult.toUserMessage(): String = when (this) {
  is PaywallResult.Purchased -> {
    if (syncStatus == PaywallSyncStatus.Synced) {
      "Purchase completed: $productId"
    } else {
      "Purchase completed, but sync failed: $productId"
    }
  }
  is PaywallResult.Restored -> {
    if (syncStatus == PaywallSyncStatus.Synced) {
      "Restored $count purchase(s)"
    } else {
      "Restored $count purchase(s), but sync failed"
    }
  }
  PaywallResult.Cancelled -> "Purchase cancelled"
  PaywallResult.Pending -> "Purchase is pending"
  is PaywallResult.Failed -> message
}

private class DemoBillingStore : BillingStoreContract {
  override suspend fun queryProducts(productIds: List<String>): List<BillingProduct> {
    return AndroidBillingContract.PRODUCT_IDS.map { productId ->
      BillingProduct(
        id = productId,
        type = if (productId == "monthly" || productId == "annual") {
          BillingProductType.Subscription
        } else {
          BillingProductType.OneTime
        },
      )
    }
  }

  override suspend fun purchase(productId: String): StorePurchaseOutcome {
    val simulatedState = when (productId) {
      "remove_ads" -> StorePurchaseState.PENDING
      else -> StorePurchaseState.PURCHASED
    }

    return when (simulatedState) {
      StorePurchaseState.PURCHASED -> StorePurchaseOutcome.Success(
        purchase = StorePurchase(
          productId = productId,
          purchaseToken = "token-$productId",
          orderId = "order-$productId",
          purchaseState = StorePurchaseState.PURCHASED,
          packageName = "com.example.androidtemplate",
        ),
      )
      StorePurchaseState.PENDING -> StorePurchaseOutcome.Pending
      StorePurchaseState.USER_CANCELED -> StorePurchaseOutcome.Cancelled
      StorePurchaseState.FAILED,
      StorePurchaseState.REFUNDED,
      StorePurchaseState.EXPIRED -> StorePurchaseOutcome.Failed("Purchase failed for $productId")
    }
  }

  override suspend fun restore(): List<StorePurchase> {
    return listOf(
      StorePurchase(
        productId = "monthly",
        purchaseToken = "restore-monthly-token",
        orderId = "restore-monthly-order",
        purchaseState = StorePurchaseState.PURCHASED,
        packageName = "com.example.androidtemplate",
      ),
    )
  }
}

private object NoopBillingSyncService : BillingSyncContract {
  override suspend fun syncPurchase(purchase: StorePurchase): BillingSyncResult = BillingSyncResult.Skipped
}
