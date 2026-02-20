package com.example.androidtemplate.features.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
  onOpenPaywall: () -> Unit,
  paywallResultMessage: String?,
) {
  val modules = listOf("Feed", "Chat", "Tools", "Tasks")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(rememberScrollState())
      .padding(top = 24.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
    ) {
      Text(
        text = "Home",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.SemiBold,
      )
      FilledTonalButton(
        onClick = onOpenPaywall,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurface,
        ),
      ) {
        Text("Upgrade")
      }
    }

    if (!paywallResultMessage.isNullOrBlank()) {
      Text(text = paywallResultMessage, style = MaterialTheme.typography.bodyMedium)
    }

    Text(
      text = "Core Features",
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
      color = MaterialTheme.colorScheme.surfaceVariant,
      tonalElevation = 0.dp,
    ) {
      Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
        modules.forEachIndexed { index, module ->
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 6.dp),
          ) {
            Text(
              text = module,
              style = MaterialTheme.typography.bodyLarge,
              fontWeight = FontWeight.SemiBold,
              color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(2.dp))
            Text(
              text = "Template module",
              style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
              color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
          }

          if (index != modules.lastIndex) {
            HorizontalDivider(
              thickness = 1.dp,
              color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
            )
          }
        }
      }
    }
  }
}
