package com.example.androidtemplate.features.mypage

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SubscriptionReducerTest {

  @Test
  fun onCancelRequested_marksCancellingWhenSubscriptionIsActive() {
    val initial = SubscriptionState.premium(
      planName = "monthly",
      renewalDate = "2026-03-21",
    )

    val next = SubscriptionReducer(initial).onCancelRequested()

    assertThat(next.isCancelling).isTrue()
    assertThat(next.isSubscribed).isTrue()
  }

  @Test
  fun onCancelCompleted_movesStateToFreePlan() {
    val initial = SubscriptionState(
      isSubscribed = true,
      planName = "annual",
      renewalDate = "2027-02-21",
      isCancelling = true,
    )

    val next = SubscriptionReducer(initial).onCancelCompleted()

    assertThat(next).isEqualTo(SubscriptionState.free())
  }
}
