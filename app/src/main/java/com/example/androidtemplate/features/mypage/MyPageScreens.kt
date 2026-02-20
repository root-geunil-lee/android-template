package com.example.androidtemplate.features.mypage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtemplate.ui.theme.IosPalette
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun MyPageRoute(
  onEditProfile: () -> Unit,
  onSubscription: () -> Unit,
  onPurchaseHistory: () -> Unit,
  onTerms: () -> Unit,
  onPrivacy: () -> Unit,
  onLogoutCompleted: () -> Unit,
  onDeleteCompleted: () -> Unit,
) {
  var showLogoutDialog by rememberSaveable { mutableStateOf(false) }
  var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

  BackHandler(enabled = showLogoutDialog || showDeleteDialog) {
    showLogoutDialog = false
    showDeleteDialog = false
  }

  MyPageScreen(
    onEditProfile = onEditProfile,
    onSubscription = onSubscription,
    onPurchaseHistory = onPurchaseHistory,
    onTerms = onTerms,
    onPrivacy = onPrivacy,
    onLogout = { showLogoutDialog = true },
    onDeleteAccount = { showDeleteDialog = true },
  )

  if (showLogoutDialog) {
    AlertDialog(
      onDismissRequest = { showLogoutDialog = false },
      confirmButton = {
        TextButton(
          onClick = {
            showLogoutDialog = false
            onLogoutCompleted()
          },
        ) {
          Text("Log out", color = IosPalette.SystemRed)
        }
      },
      dismissButton = {
        TextButton(onClick = { showLogoutDialog = false }) {
          Text("Cancel", color = IosPalette.Label)
        }
      },
      title = { Text("Log out?", color = IosPalette.Label) },
      containerColor = IosPalette.SecondarySystemGroupedBackground,
    )
  }

  if (showDeleteDialog) {
    AlertDialog(
      onDismissRequest = { showDeleteDialog = false },
      confirmButton = {
        TextButton(
          onClick = {
            showDeleteDialog = false
            onDeleteCompleted()
          },
        ) {
          Text("Delete Account", color = IosPalette.SystemRed)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteDialog = false }) {
          Text("Cancel", color = IosPalette.Label)
        }
      },
      title = { Text("Delete account?", color = IosPalette.Label) },
      text = { Text("This action cannot be undone.", color = IosPalette.SecondaryLabel) },
      containerColor = IosPalette.SecondarySystemGroupedBackground,
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
    modifier = Modifier
      .fillMaxSize()
      .background(IosPalette.SystemGroupedBackground)
      .padding(horizontal = 20.dp)
      .testTag("mypage_scroll"),
    contentPadding = PaddingValues(top = 20.dp, bottom = 24.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp),
  ) {
    item {
      Text(
        text = "My Page",
        style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.Label,
      )
    }

    item {
      IosSectionCard {
        AccountHeaderRow(
          displayName = "User",
          email = "No email",
        )
      }
    }

    item {
      SectionTitle("Profile")
      IosSectionCard {
        NavRow(
          text = "Edit Profile",
          onClick = onEditProfile,
        )
      }
    }

    item {
      SectionTitle("Settings")
      IosSectionCard {
        NavRow(
          text = "Subscription",
          onClick = onSubscription,
        )
        SectionDivider()
        NavRow(
          text = "Purchase History",
          onClick = onPurchaseHistory,
        )
      }
    }

    item {
      SectionTitle("Support")
      IosSectionCard {
        NavRow(
          text = "Terms",
          onClick = onTerms,
        )
        SectionDivider()
        NavRow(
          text = "Privacy",
          onClick = onPrivacy,
        )
      }
    }

    item {
      SectionTitle("Danger Zone")
      IosSectionCard {
        NavRow(
          text = "Log out",
          onClick = onLogout,
          destructive = true,
          chevron = false,
        )
        SectionDivider()
        NavRow(
          text = "Delete Account",
          onClick = onDeleteAccount,
          destructive = true,
          chevron = false,
        )
      }
    }
  }
}

@Composable
private fun AccountHeaderRow(
  displayName: String,
  email: String,
) {
  val initial = displayName.firstOrNull()?.uppercase() ?: "U"
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 6.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Box(
      modifier = Modifier
        .size(44.dp)
        .background(IosPalette.TertiarySystemFill, CircleShape),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = initial,
        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.SecondaryLabel,
      )
    }

    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = displayName,
        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.Label,
      )
      Text(
        text = email,
        style = TextStyle(fontSize = 15.sp),
        color = IosPalette.SecondaryLabel,
      )
    }

    Icon(
      imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
      contentDescription = null,
      tint = IosPalette.TertiaryLabel,
    )
  }
}

@Composable
fun SubscriptionScreen(
  onBack: () -> Unit,
  onOpenPlanSelection: () -> Unit,
  onOpenPaymentMethod: () -> Unit,
  onOpenStoreSubscription: () -> Unit,
) {
  val coroutineScope = rememberCoroutineScope()
  var state by remember { mutableStateOf(SubscriptionState.premium(planName = "Premium")) }
  var showCancelDialog by rememberSaveable { mutableStateOf(false) }

  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Subscription",
        onBack = onBack,
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp)
        .testTag("subscription_scroll"),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        IosSectionCard {
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
              text = state.planName,
              style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
              color = IosPalette.Label,
            )
            Text(
              text = state.renewalDate?.let { "Renews on $it" } ?: "Upgrade to unlock premium features",
              style = TextStyle(fontSize = 15.sp),
              color = IosPalette.SecondaryLabel,
            )
            if (state.isSubscribed) {
              Text(
                text = "Active",
                style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
                color = IosPalette.SecondaryLabel,
              )
            }
          }
        }
      }

      item {
        SectionTitle("Manage")
        IosSectionCard {
          NavRow(text = "Change Plan", onClick = onOpenPlanSelection)
          SectionDivider()
          NavRow(text = "Payment Method", onClick = onOpenPaymentMethod)
          SectionDivider()
          NavRow(text = "Manage in App Store", onClick = onOpenStoreSubscription)
          if (state.isSubscribed) {
            SectionDivider()
            NavRow(
              text = "Cancel Subscription",
              onClick = { showCancelDialog = true },
              destructive = true,
              chevron = false,
              trailing = {
                if (state.isCancelling) {
                  CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = IosPalette.SecondaryLabel,
                  )
                }
              },
            )
          }
        }
      }

      item {
        Text(
          text = "Subscriptions are managed through your Apple ID settings.",
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SecondaryLabel,
        )
      }
    }
  }

  if (showCancelDialog) {
    AlertDialog(
      onDismissRequest = { showCancelDialog = false },
      confirmButton = {
        TextButton(
          onClick = {
            showCancelDialog = false
            coroutineScope.launch {
              state = SubscriptionReducer(state).onCancelRequested()
              delay(800)
              state = SubscriptionReducer(state).onCancelCompleted()
            }
          },
        ) {
          Text("Cancel Subscription", color = IosPalette.SystemRed)
        }
      },
      dismissButton = {
        TextButton(onClick = { showCancelDialog = false }) {
          Text("Keep Subscription", color = IosPalette.Label)
        }
      },
      title = { Text("Cancel subscription?", color = IosPalette.Label) },
      text = { Text("You’ll keep access until the end of your billing period.", color = IosPalette.SecondaryLabel) },
      containerColor = IosPalette.SecondarySystemGroupedBackground,
    )
  }
}

@Composable
fun PlanSelectionScreen(
  onBack: () -> Unit,
) {
  var selectedPlanId by rememberSaveable { mutableStateOf("annual") }
  val options = remember {
    listOf(
      PlanOption(id = "monthly", title = "Monthly", price = "$9.99 / month"),
      PlanOption(id = "annual", title = "Annual", price = "$79.99 / year"),
      PlanOption(id = "remove_ads", title = "Remove Ads", price = "$4.99"),
      PlanOption(id = "lifetime", title = "Lifetime", price = "$99.99"),
    )
  }

  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Change Plan",
        onBack = onBack,
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        IosSectionCard {
          options.forEachIndexed { index, option ->
            NavRow(
              text = option.title,
              subtitle = option.price,
              onClick = { selectedPlanId = option.id },
              trailing = {
                if (selectedPlanId == option.id) {
                  Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = IosPalette.SystemBlue,
                    modifier = Modifier.size(18.dp),
                  )
                }
              },
              chevron = false,
            )
            if (index != options.lastIndex) {
              SectionDivider()
            }
          }
        }
      }
    }
  }
}

@Composable
fun PaymentMethodScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Payment Method",
        onBack = onBack,
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        IosSectionCard {
          LabeledValueRow(label = "Current Method", value = "Visa •••• 4242")
        }
      }
      item {
        IosSectionCard {
          NavRow(
            text = "Add Payment Method",
            onClick = {},
            chevron = false,
          )
        }
      }
    }
  }
}

@Composable
fun PurchaseHistoryScreen(
  onBack: () -> Unit,
  onOpenTransaction: (String) -> Unit,
) {
  val records = samplePurchaseRecords()
  var activeFilter by rememberSaveable { mutableStateOf(PurchaseFilter.All) }
  val filteredRecords = filterPurchases(records, activeFilter)

  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Purchase History",
        onBack = onBack,
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        IosSectionCard {
          SegmentedFilter(
            selected = activeFilter,
            onSelected = { activeFilter = it },
          )
        }
      }

      item {
        if (filteredRecords.isEmpty()) {
          IosSectionCard {
            Text(
              text = "No purchases yet.",
              style = TextStyle(fontSize = 17.sp),
              color = IosPalette.SecondaryLabel,
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
              textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
          }
        } else {
          IosSectionCard {
            filteredRecords.forEachIndexed { index, record ->
              NavRow(
                text = record.productId,
                subtitle = record.purchasedAt,
                onClick = { onOpenTransaction(record.id) },
              )
              if (index != filteredRecords.lastIndex) {
                SectionDivider()
              }
            }
          }
        }
      }
    }
  }
}

@Composable
fun EditProfileScreen(
  onBack: () -> Unit,
) {
  var state by rememberSaveable {
    mutableStateOf(EditProfileState.initial(email = "user@example.com"))
  }
  var showDeleteConfirm by rememberSaveable { mutableStateOf(false) }

  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Edit Profile",
        onBack = onBack,
        action = {
          TextButton(
            onClick = onBack,
            enabled = state.isSaveEnabled,
          ) {
            Text("Save", color = if (state.isSaveEnabled) IosPalette.SystemBlue else IosPalette.SecondaryLabel)
          }
        },
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        IosSectionCard {
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .background(IosPalette.TertiarySystemFill, CircleShape),
              contentAlignment = Alignment.Center,
            ) {
              Text("U", color = IosPalette.SecondaryLabel, fontWeight = FontWeight.SemiBold)
            }
            TextButton(
              onClick = {},
              contentPadding = PaddingValues(0.dp),
            ) {
              Text("Change Photo", color = IosPalette.SystemBlue)
            }
          }
        }
      }

      item {
        SectionTitle("Profile")
        IosSectionCard {
          OutlinedTextField(
            value = state.displayName,
            onValueChange = { value ->
              state = EditProfileReducer(state).onDisplayNameChanged(value)
            },
            label = { Text("Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            isError = state.validationError != null,
          )
          Spacer(Modifier.height(12.dp))
          OutlinedTextField(
            value = state.email,
            onValueChange = {},
            label = { Text("Email") },
            singleLine = true,
            enabled = false,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      }

      item {
        SectionTitle("Danger Zone")
        IosSectionCard {
          NavRow(
            text = "Delete Account",
            onClick = { showDeleteConfirm = true },
            destructive = true,
            chevron = false,
          )
        }
      }
    }
  }

  if (showDeleteConfirm) {
    AlertDialog(
      onDismissRequest = { showDeleteConfirm = false },
      confirmButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text("Delete Account", color = IosPalette.SystemRed)
        }
      },
      dismissButton = {
        TextButton(onClick = { showDeleteConfirm = false }) {
          Text("Cancel", color = IosPalette.Label)
        }
      },
      title = { Text("Delete account?", color = IosPalette.Label) },
      text = { Text("This action cannot be undone.", color = IosPalette.SecondaryLabel) },
      containerColor = IosPalette.SecondarySystemGroupedBackground,
    )
  }
}

@Composable
fun TransactionDetailScreen(
  transactionId: String,
  onBack: () -> Unit,
) {
  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Transaction",
        onBack = onBack,
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      item {
        IosSectionCard {
          LabeledValueRow(label = "Title", value = transactionId)
          SectionDivider()
          LabeledValueRow(label = "Date", value = "Feb 20, 2026")
          SectionDivider()
          LabeledValueRow(label = "Amount", value = "$9.99")
          SectionDivider()
          LabeledValueRow(label = "Type", value = "Subscription")
          SectionDivider()
          LabeledValueRow(label = "Status", value = "Completed")
        }
      }
      item {
        IosSectionCard {
          NavRow(
            text = "Get receipt",
            onClick = {},
            chevron = false,
          )
        }
      }
    }
  }
}

@Composable
fun TermsScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Terms",
        onBack = onBack,
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
    ) {
      item {
        IosSectionCard {
          Text(
            text = "Terms of Service",
            style = TextStyle(fontSize = 17.sp),
            color = IosPalette.Label,
          )
        }
      }
    }
  }
}

@Composable
fun PrivacyScreen(
  onBack: () -> Unit,
) {
  Scaffold(
    topBar = {
      InlineTopBar(
        title = "Privacy",
        onBack = onBack,
      )
    },
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .padding(horizontal = 20.dp),
      contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
    ) {
      item {
        IosSectionCard {
          Text(
            text = "Privacy Policy",
            style = TextStyle(fontSize = 17.sp),
            color = IosPalette.Label,
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun InlineTopBar(
  title: String,
  onBack: () -> Unit,
  action: @Composable (() -> Unit)? = null,
) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = title,
        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.Label,
      )
    },
    navigationIcon = {
      IconButton(onClick = onBack) {
        Icon(
          imageVector = Icons.AutoMirrored.Filled.ArrowBack,
          contentDescription = "Back",
          tint = IosPalette.Label,
        )
      }
    },
    actions = {
      if (action != null) {
        action()
      }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = IosPalette.SystemGroupedBackground,
      titleContentColor = IosPalette.Label,
      navigationIconContentColor = IosPalette.Label,
      actionIconContentColor = IosPalette.SystemBlue,
    ),
  )
}

@Composable
private fun IosSectionCard(
  content: @Composable ColumnScope.() -> Unit,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(16.dp))
      .padding(horizontal = 14.dp, vertical = 8.dp),
    content = content,
  )
}

@Composable
private fun SectionTitle(text: String) {
  Text(
    text = text,
    style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
    color = IosPalette.SecondaryLabel,
  )
}

@Composable
private fun SectionDivider() {
  HorizontalDivider(
    thickness = 1.dp,
    color = IosPalette.Separator.copy(alpha = 0.18f),
    modifier = Modifier.padding(start = 4.dp),
  )
}

@Composable
private fun LabeledValueRow(
  label: String,
  value: String,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 12.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = label,
      style = TextStyle(fontSize = 15.sp),
      color = IosPalette.SecondaryLabel,
    )
    Text(
      text = value,
      style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
      color = IosPalette.Label,
    )
  }
}

@Composable
private fun SegmentedFilter(
  selected: PurchaseFilter,
  onSelected: (PurchaseFilter) -> Unit,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(IosPalette.SystemGroupedBackground, RoundedCornerShape(10.dp))
      .padding(3.dp),
    horizontalArrangement = Arrangement.spacedBy(4.dp),
  ) {
    SegmentedItem(
      text = "All",
      selected = selected == PurchaseFilter.All,
      onClick = { onSelected(PurchaseFilter.All) },
      modifier = Modifier.weight(1f),
    )
    SegmentedItem(
      text = "Subscriptions",
      selected = selected == PurchaseFilter.Subscriptions,
      onClick = { onSelected(PurchaseFilter.Subscriptions) },
      modifier = Modifier.weight(1f),
    )
    SegmentedItem(
      text = "One-time",
      selected = selected == PurchaseFilter.OneTime,
      onClick = { onSelected(PurchaseFilter.OneTime) },
      modifier = Modifier.weight(1f),
    )
  }
}

@Composable
private fun SegmentedItem(
  text: String,
  selected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Box(
    modifier = modifier
      .background(
        color = if (selected) IosPalette.SecondarySystemGroupedBackground else Color.Transparent,
        shape = RoundedCornerShape(8.dp),
      )
      .clickable(onClick = onClick)
      .padding(vertical = 8.dp),
    contentAlignment = Alignment.Center,
  ) {
    Text(
      text = text,
      style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium),
      color = IosPalette.Label,
    )
  }
}

@Composable
private fun NavRow(
  text: String,
  onClick: () -> Unit,
  subtitle: String? = null,
  destructive: Boolean = false,
  chevron: Boolean = true,
  trailing: @Composable (() -> Unit)? = null,
) {
  val rowTag = "row_item_${text.replace(" ", "_")}"

  Row(
    modifier = Modifier
      .fillMaxWidth()
      .heightIn(min = 48.dp)
      .clickable(onClick = onClick)
      .padding(vertical = 12.dp)
      .semantics(mergeDescendants = true) {
        contentDescription = text
        role = Role.Button
      }
      .testTag(rowTag),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    Column(
      modifier = Modifier.weight(1f),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = text,
        style = TextStyle(fontSize = 17.sp),
        color = if (destructive) IosPalette.SystemRed else IosPalette.Label,
      )
      if (!subtitle.isNullOrBlank()) {
        Text(
          text = subtitle,
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SecondaryLabel,
        )
      }
    }

    if (trailing != null) {
      trailing()
    } else if (chevron) {
      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = IosPalette.TertiaryLabel,
      )
    }
  }
}

private data class PlanOption(
  val id: String,
  val title: String,
  val price: String,
)

private fun samplePurchaseRecords(): List<PurchaseRecord> {
  return listOf(
    PurchaseRecord(id = "tx_1", productId = "Premium Monthly", category = PurchaseCategory.Subscription, purchasedAt = "Feb 18, 2026 · $9.99"),
    PurchaseRecord(id = "tx_2", productId = "Premium Monthly", category = PurchaseCategory.Subscription, purchasedAt = "Jan 18, 2026 · $9.99"),
    PurchaseRecord(id = "tx_3", productId = "One-time Add-on", category = PurchaseCategory.OneTime, purchasedAt = "Jan 10, 2026 · $14.99"),
    PurchaseRecord(id = "tx_4", productId = "Coin Pack", category = PurchaseCategory.OneTime, purchasedAt = "Dec 20, 2025 · $4.99"),
  )
}
