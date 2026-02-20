package com.example.androidtemplate.features.billing

import com.example.androidtemplate.core.ui.UiEvent
import com.example.androidtemplate.core.ui.UiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

const val PAYWALL_SHEET_ID = "paywall"

data class PaywallUiModel(
  val products: List<BillingProduct>,
  val isProcessing: Boolean,
  val lastResult: PaywallResult?,
  val resultMessage: String?,
)

class PaywallUiStore(
  private val useCase: PaywallFlowUseCase,
) {
  private val _events = MutableSharedFlow<UiEvent>(extraBufferCapacity = 16)
  val events: Flow<UiEvent> = _events

  var state: UiState<PaywallUiModel> = UiState.Idle
    private set

  suspend fun load() {
    state = UiState.Loading
    state = useCase.loadProducts().toUiState()
  }

  suspend fun purchase(productId: String) {
    val nextState = useCase.purchase(productId)
    state = nextState.toUiState()
    emitResultEvent((nextState as? PaywallState.Ready)?.lastResult)
  }

  suspend fun restore() {
    val nextState = useCase.restorePurchases()
    state = nextState.toUiState()
    emitResultEvent((nextState as? PaywallState.Ready)?.lastResult)
  }

  private suspend fun emitResultEvent(result: PaywallResult?) {
    when (result) {
      is PaywallResult.Purchased,
      is PaywallResult.Restored -> _events.emit(UiEvent.DismissSheet(PAYWALL_SHEET_ID))
      PaywallResult.Cancelled -> _events.emit(UiEvent.ShowSnackbar("Purchase cancelled"))
      PaywallResult.Pending -> _events.emit(UiEvent.ShowSnackbar("Purchase is pending"))
      is PaywallResult.Failed -> _events.emit(UiEvent.ShowSnackbar(result.message))
      null -> Unit
    }
  }
}

private fun PaywallState.toUiState(): UiState<PaywallUiModel> {
  return when (this) {
    PaywallState.Idle -> UiState.Idle
    PaywallState.LoadingProducts -> UiState.Loading
    is PaywallState.Processing -> UiState.Loading
    is PaywallState.Ready -> UiState.Success(
      PaywallUiModel(
        products = products,
        isProcessing = false,
        lastResult = lastResult,
        resultMessage = lastResult?.toUserMessage(),
      ),
    )
  }
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
