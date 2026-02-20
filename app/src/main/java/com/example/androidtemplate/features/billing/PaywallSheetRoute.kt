package com.example.androidtemplate.features.billing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtemplate.core.contracts.AndroidBillingContract
import com.example.androidtemplate.core.ui.UiEvent
import com.example.androidtemplate.core.ui.UiState
import com.example.androidtemplate.ui.theme.IosPalette
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallSheetRoute(
  onClose: () -> Unit,
  onResult: (PaywallResult) -> Unit,
  syncService: BillingSyncContract = NoopBillingSyncService,
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
  val coroutineScope = rememberCoroutineScope()
  val uriHandler = LocalUriHandler.current
  val uiStore = remember {
    PaywallUiStore(
      useCase = PaywallFlowUseCase(
        billingStore = DemoBillingStore(),
        syncService = syncService,
      ),
    )
  }

  var state by remember { mutableStateOf<UiState<PaywallUiModel>>(UiState.Idle) }
  var cachedProducts by remember { mutableStateOf(emptyList<BillingProduct>()) }
  var transientMessage by remember { mutableStateOf<String?>(null) }
  var selectedProductId by rememberSaveable { mutableStateOf("annual") }

  LaunchedEffect(Unit) {
    uiStore.load()
    state = uiStore.state
  }

  LaunchedEffect(uiStore) {
    uiStore.events.collect { event ->
      when (event) {
        is UiEvent.DismissSheet -> {
          val result = (uiStore.state as? UiState.Success<PaywallUiModel>)?.data?.lastResult
          if (result != null) {
            onResult(result)
          }
          onClose()
        }
        is UiEvent.ShowSnackbar -> transientMessage = event.message
        else -> Unit
      }
    }
  }

  if (state is UiState.Success) {
    cachedProducts = (state as UiState.Success<PaywallUiModel>).data.products
  }

  val products = if (state is UiState.Success) {
    (state as UiState.Success<PaywallUiModel>).data.products
  } else {
    cachedProducts
  }

  val visibleOptions = remember(products) {
    val ids = products.map { it.id }.toSet()
    val filtered = PaywallOptions.filter { option -> option.id in ids }
    if (filtered.isEmpty()) PaywallOptions else filtered
  }

  LaunchedEffect(visibleOptions) {
    if (visibleOptions.none { it.id == selectedProductId }) {
      selectedProductId = visibleOptions.firstOrNull()?.id ?: ""
    }
  }

  val selectedOption = visibleOptions.firstOrNull { it.id == selectedProductId }
  val isProcessing = state == UiState.Loading
  val resultMessage = (state as? UiState.Success<PaywallUiModel>)?.data?.resultMessage ?: transientMessage
  val isErrorMessage = resultMessage?.contains("failed", ignoreCase = true) == true ||
    resultMessage?.contains("error", ignoreCase = true) == true

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
    containerColor = IosPalette.SystemGroupedBackground,
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .windowInsetsPadding(WindowInsets.navigationBars)
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        TextButton(
          onClick = {
            onResult(PaywallResult.Cancelled)
            onClose()
          },
          contentPadding = PaddingValues(0.dp),
        ) {
          Text("Not now", color = IosPalette.Label)
        }
      }

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
          text = "Go Premium",
          style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.SemiBold),
          color = IosPalette.Label,
        )
        Text(
          text = "Unlock more features and higher limits across this app template.",
          style = TextStyle(fontSize = 17.sp),
          color = IosPalette.SecondaryLabel,
        )
        Text(
          text = "Cancel anytime",
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SecondaryLabel,
        )
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(16.dp))
          .padding(horizontal = 14.dp, vertical = 8.dp),
      ) {
        Text(
          text = "Benefits",
          style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
          color = IosPalette.Label,
          modifier = Modifier.padding(vertical = 8.dp),
        )
        PaywallBenefits.forEachIndexed { index, benefit ->
          Row(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
              imageVector = Icons.Filled.Check,
              contentDescription = null,
              tint = IosPalette.SecondaryLabel,
              modifier = Modifier.size(16.dp),
            )
            Text(
              text = benefit,
              style = TextStyle(fontSize = 15.sp),
              color = IosPalette.Label,
            )
          }
          if (index != PaywallBenefits.lastIndex) {
            HorizontalDivider(
              color = IosPalette.Separator.copy(alpha = 0.16f),
              thickness = 1.dp,
            )
          }
        }
      }

      Text(
        text = "Choose an option",
        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.SecondaryLabel,
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(16.dp))
          .padding(horizontal = 14.dp, vertical = 8.dp),
      ) {
        visibleOptions.forEachIndexed { index, option ->
          PaywallOptionRow(
            option = option,
            selected = option.id == selectedProductId,
            onClick = { selectedProductId = option.id },
          )
          if (index != visibleOptions.lastIndex) {
            HorizontalDivider(
              color = IosPalette.Separator.copy(alpha = 0.16f),
              thickness = 1.dp,
            )
          }
        }
      }

      if (selectedOption != null) {
        Text(
          text = selectedOption.footer,
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SecondaryLabel,
        )
      }

      Button(
        onClick = {
          val targetId = selectedOption?.id ?: return@Button
          coroutineScope.launch {
            transientMessage = null
            uiStore.purchase(targetId)
            state = uiStore.state
          }
        },
        enabled = !isProcessing && selectedOption != null,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.Black,
          contentColor = Color.White,
          disabledContainerColor = Color.Black.copy(alpha = 0.2f),
          disabledContentColor = Color.White.copy(alpha = 0.6f),
        ),
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
      ) {
        if (isProcessing) {
          CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
          )
        } else {
          Text(primaryButtonTitle(selectedOption))
        }
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TextButton(
          onClick = {
            coroutineScope.launch {
              transientMessage = null
              uiStore.restore()
              state = uiStore.state
            }
          },
          enabled = !isProcessing,
          contentPadding = PaddingValues(0.dp),
        ) {
          Text("Restore purchases", color = IosPalette.Label, style = TextStyle(fontSize = 13.sp))
        }

        Spacer(Modifier.weight(1f))

        TextButton(
          onClick = { uriHandler.openUri("https://example.com/terms") },
          contentPadding = PaddingValues(0.dp),
        ) {
          Text("Terms", color = IosPalette.Label, style = TextStyle(fontSize = 13.sp))
        }

        Spacer(Modifier.size(16.dp))

        TextButton(
          onClick = { uriHandler.openUri("https://example.com/privacy") },
          contentPadding = PaddingValues(0.dp),
        ) {
          Text("Privacy", color = IosPalette.Label, style = TextStyle(fontSize = 13.sp))
        }
      }

      if (!resultMessage.isNullOrBlank()) {
        Text(
          text = resultMessage,
          style = TextStyle(fontSize = 13.sp),
          color = if (isErrorMessage) IosPalette.SystemRed else IosPalette.SecondaryLabel,
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Start,
        )
      }

      Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
    }
  }
}

private fun primaryButtonTitle(option: PaywallOption?): String {
  return when (option?.id) {
    "monthly", "annual" -> "Start subscription"
    "remove_ads" -> "Remove ads"
    "lifetime" -> "Purchase"
    else -> "Continue"
  }
}

private data class PaywallOption(
  val id: String,
  val title: String,
  val subtitle: String,
  val priceText: String,
  val recommended: Boolean,
  val footer: String,
)

private val PaywallOptions = listOf(
  PaywallOption(
    id = "monthly",
    title = "Monthly",
    subtitle = "Cancel anytime",
    priceText = "$9.99 / month",
    recommended = false,
    footer = "Renews monthly until cancelled.",
  ),
  PaywallOption(
    id = "annual",
    title = "Annual",
    subtitle = "Best yearly savings",
    priceText = "$79.99 / year",
    recommended = true,
    footer = "Renews yearly until cancelled.",
  ),
  PaywallOption(
    id = "remove_ads",
    title = "Remove Ads",
    subtitle = "One-time purchase",
    priceText = "$4.99",
    recommended = false,
    footer = "One-time purchase. Removes ads on this device/account.",
  ),
  PaywallOption(
    id = "lifetime",
    title = "Lifetime",
    subtitle = "One-time purchase",
    priceText = "$99.99",
    recommended = false,
    footer = "One-time purchase. No renewal.",
  ),
)

private val PaywallBenefits = listOf(
  "Unlimited core features",
  "Priority sync and faster processing",
  "Cross-device access",
  "Premium support",
)

@Composable
private fun PaywallOptionRow(
  option: PaywallOption,
  selected: Boolean,
  onClick: () -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 10.dp),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
      ) {
        Text(
          text = option.title,
          style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium),
          color = IosPalette.Label,
        )
        if (option.recommended) {
          Text(
            text = "Best value",
            style = TextStyle(fontSize = 12.sp),
            color = IosPalette.SecondaryLabel,
          )
        }
      }
      Text(
        text = option.priceText,
        style = TextStyle(fontSize = 15.sp),
        color = IosPalette.Label,
      )
      Text(
        text = option.subtitle,
        style = TextStyle(fontSize = 13.sp),
        color = IosPalette.SecondaryLabel,
      )
    }

    Box(
      modifier = Modifier
        .size(22.dp)
        .background(
          color = if (selected) IosPalette.SystemBlue else Color.Transparent,
          shape = CircleShape,
        )
        .padding(3.dp),
      contentAlignment = Alignment.Center,
    ) {
      if (selected) {
        Icon(
          imageVector = Icons.Filled.Check,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(14.dp),
        )
      } else {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color.Transparent),
        )
      }
    }
  }
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
