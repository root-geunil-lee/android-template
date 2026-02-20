package com.example.androidtemplate.features.mypage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MyPageRoute(
  onSubscription: () -> Unit,
  onPurchaseHistory: () -> Unit,
  onLogoutCompleted: () -> Unit,
  onDeleteCompleted: () -> Unit,
) {
  var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
  var showDeleteDialogStep1 by rememberSaveable { mutableStateOf(false) }
  var showDeleteDialogStep2 by rememberSaveable { mutableStateOf(false) }

  MyPageScreen(
    onEditProfile = {},
    onSubscription = onSubscription,
    onPurchaseHistory = onPurchaseHistory,
    onTerms = {},
    onPrivacy = {},
    onLogout = { showLogoutDialog = true },
    onDeleteAccount = { showDeleteDialogStep1 = true },
  )

  if (showLogoutDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutDialog = false },
      confirmButton = {
        Button(onClick = {
          showLogoutDialog = false
          onLogoutCompleted()
        }) {
          Text("Log out")
        }
      },
      dismissButton = {
        Button(onClick = { showLogoutDialog = false }) { Text("Cancel") }
      },
      title = { Text("Confirm logout") },
      text = { Text("Do you want to log out?") },
    )
  }

  if (showDeleteDialogStep1) {
    AlertDialog(
      onDismissRequest = { showDeleteDialogStep1 = false },
      confirmButton = {
        Button(onClick = {
          showDeleteDialogStep1 = false
          showDeleteDialogStep2 = true
        }) {
          Text("Continue")
        }
      },
      dismissButton = {
        Button(onClick = { showDeleteDialogStep1 = false }) { Text("Cancel") }
      },
      title = { Text("Delete account") },
      text = { Text("This action cannot be undone.") },
    )
  }

  if (showDeleteDialogStep2) {
    AlertDialog(
      onDismissRequest = { showDeleteDialogStep2 = false },
      confirmButton = {
        Button(onClick = {
          showDeleteDialogStep2 = false
          onDeleteCompleted()
        }) {
          Text("Delete now")
        }
      },
      dismissButton = {
        Button(onClick = { showDeleteDialogStep2 = false }) { Text("Keep account") }
      },
      title = { Text("Final confirmation") },
      text = { Text("Delete account permanently?") },
    )
  }
}

@Composable
fun MyPageScreen(
  onEditProfile: () -> Unit,
  onSubscription: () -> Unit,
  onPurchaseHistory: () -> Unit,
  onTerms: () -> Unit,
  onPrivacy: () -> Unit,
  onLogout: () -> Unit,
  onDeleteAccount: () -> Unit,
) {
  LazyColumn(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    item { SectionTitle("Profile") }
    item { RowItem("Edit Profile", onClick = onEditProfile) }

    item { SectionTitle("Settings") }
    item { RowItem("Subscription", onClick = onSubscription) }
    item { RowItem("Purchase History", onClick = onPurchaseHistory) }

    item { SectionTitle("Support") }
    item { RowItem("Terms", onClick = onTerms) }
    item { RowItem("Privacy", onClick = onPrivacy) }

    item { SectionTitle("Danger Zone") }
    item { RowItem("Log out", onClick = onLogout, destructive = true) }
    item { RowItem("Delete Account", onClick = onDeleteAccount, destructive = true) }
  }
}

@Composable
fun SubscriptionScreen(onBack: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Subscription", style = MaterialTheme.typography.headlineSmall)
    Text("Current plan: monthly")
    Spacer(Modifier.height(16.dp))
    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
  }
}

@Composable
fun PurchaseHistoryScreen(onBack: () -> Unit) {
  Column(
    modifier = Modifier.fillMaxSize(),
    verticalArrangement = Arrangement.Center,
  ) {
    Text("Purchase History", style = MaterialTheme.typography.headlineSmall)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      Text("All")
      Text("Subscriptions")
      Text("One-time")
    }
    Spacer(Modifier.height(16.dp))
    Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
  }
}

@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.titleMedium,
    fontWeight = FontWeight.SemiBold,
    modifier = Modifier.padding(top = 16.dp),
  )
}

@Composable
private fun RowItem(
  text: String,
  onClick: () -> Unit,
  destructive: Boolean = false,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
  ) {
    Text(
      text = text,
      color = if (destructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
    )
    Text(
      text = ">",
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}
