package com.example.androidtemplate.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
  onOpenPaywall: () -> Unit,
  paywallResultMessage: String?,
) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Home", style = MaterialTheme.typography.headlineMedium)
    if (!paywallResultMessage.isNullOrBlank()) {
      Text(
        text = paywallResultMessage,
        style = MaterialTheme.typography.bodyMedium,
      )
    }
    androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
    Button(onClick = onOpenPaywall, modifier = Modifier.fillMaxWidth()) {
      Text("Upgrade")
    }
  }
}
