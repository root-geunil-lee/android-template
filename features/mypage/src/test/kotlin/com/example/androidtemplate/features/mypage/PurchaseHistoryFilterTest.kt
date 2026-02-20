package com.example.androidtemplate.features.mypage

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class PurchaseHistoryFilterTest {

  @Test
  fun filter_all_returnsAllRecords() {
    val records = sampleRecords()

    val filtered = filterPurchases(records, PurchaseFilter.All)

    assertThat(filtered).hasSize(4)
    assertThat(filtered.map { it.id }).containsExactly("tx_1", "tx_2", "tx_3", "tx_4").inOrder()
  }

  @Test
  fun filter_subscriptions_returnsOnlySubscriptionRecords() {
    val records = sampleRecords()

    val filtered = filterPurchases(records, PurchaseFilter.Subscriptions)

    assertThat(filtered).hasSize(2)
    assertThat(filtered.all { it.category == PurchaseCategory.Subscription }).isTrue()
    assertThat(filtered.map { it.id }).containsExactly("tx_1", "tx_3").inOrder()
  }

  @Test
  fun filter_oneTime_returnsOnlyOneTimeRecords() {
    val records = sampleRecords()

    val filtered = filterPurchases(records, PurchaseFilter.OneTime)

    assertThat(filtered).hasSize(2)
    assertThat(filtered.all { it.category == PurchaseCategory.OneTime }).isTrue()
    assertThat(filtered.map { it.id }).containsExactly("tx_2", "tx_4").inOrder()
  }

  private fun sampleRecords(): List<PurchaseRecord> {
    return listOf(
      PurchaseRecord(id = "tx_1", productId = "monthly", category = PurchaseCategory.Subscription, purchasedAt = "2026-02-01"),
      PurchaseRecord(id = "tx_2", productId = "remove_ads", category = PurchaseCategory.OneTime, purchasedAt = "2026-02-02"),
      PurchaseRecord(id = "tx_3", productId = "annual", category = PurchaseCategory.Subscription, purchasedAt = "2026-02-03"),
      PurchaseRecord(id = "tx_4", productId = "lifetime", category = PurchaseCategory.OneTime, purchasedAt = "2026-02-04"),
    )
  }
}
