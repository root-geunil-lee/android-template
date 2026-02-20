package com.example.androidtemplate.features.billing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.androidtemplate.core.contracts.AndroidBillingContract

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaywallSheetRoute(onClose: () -> Unit) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

  BackHandler {
    onClose()
  }

  ModalBottomSheet(
    onDismissRequest = onClose,
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
      AndroidBillingContract.PRODUCT_IDS.forEach { productId ->
        Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
          Text(productId)
        }
      }
      Button(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
        Text("Restore purchases")
      }
      androidx.compose.foundation.layout.Spacer(Modifier.windowInsetsBottomHeight(androidx.compose.foundation.layout.WindowInsets.navigationBars))
    }
  }
}
