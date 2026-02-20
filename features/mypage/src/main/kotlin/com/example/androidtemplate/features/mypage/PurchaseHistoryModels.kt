package com.example.androidtemplate.features.mypage

enum class PurchaseCategory {
  Subscription,
  OneTime,
}

enum class PurchaseFilter {
  All,
  Subscriptions,
  OneTime,
}

data class PurchaseRecord(
  val id: String,
  val productId: String,
  val category: PurchaseCategory,
  val purchasedAt: String,
)

fun filterPurchases(
  records: List<PurchaseRecord>,
  filter: PurchaseFilter,
): List<PurchaseRecord> {
  return when (filter) {
    PurchaseFilter.All -> records
    PurchaseFilter.Subscriptions -> records.filter { it.category == PurchaseCategory.Subscription }
    PurchaseFilter.OneTime -> records.filter { it.category == PurchaseCategory.OneTime }
  }
}
