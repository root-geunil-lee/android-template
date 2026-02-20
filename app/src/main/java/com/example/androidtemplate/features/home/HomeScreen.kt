package com.example.androidtemplate.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtemplate.ui.theme.IosPalette

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
      .padding(top = 20.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Text(
        text = "Home",
        style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.Label,
      )

      TextButton(
        onClick = onOpenPaywall,
        modifier = Modifier
          .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(16.dp))
          .padding(horizontal = 2.dp),
      ) {
        Text(
          text = "Upgrade",
          style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium),
          color = IosPalette.Label,
        )
      }
    }

    Text(
      text = "Core Features",
      style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
      color = IosPalette.SecondaryLabel,
    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(16.dp))
        .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
      modules.forEachIndexed { index, module ->
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        ) {
          Text(
            text = module,
            style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
            color = IosPalette.Label,
          )
          Spacer(Modifier.height(2.dp))
          Text(
            text = "Template module",
            style = TextStyle(fontSize = 17.sp),
            color = IosPalette.SecondaryLabel,
          )
        }
        if (index != modules.lastIndex) {
          HorizontalDivider(
            thickness = 1.dp,
            color = IosPalette.Separator.copy(alpha = 0.2f),
          )
        }
      }
    }

    if (!paywallResultMessage.isNullOrBlank()) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(16.dp))
          .padding(horizontal = 16.dp, vertical = 12.dp),
      ) {
        Text(
          text = paywallResultMessage,
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SecondaryLabel,
        )
      }
    }
  }
}
