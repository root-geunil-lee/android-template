package com.example.androidtemplate.features.mypage

data class SubscriptionState(
  val isSubscribed: Boolean,
  val planName: String,
  val renewalDate: String?,
  val isCancelling: Boolean = false,
) {
  companion object {
    fun free(): SubscriptionState {
      return SubscriptionState(
        isSubscribed = false,
        planName = "Free",
        renewalDate = null,
      )
    }

    fun premium(
      planName: String = "monthly",
      renewalDate: String = "2026-03-21",
    ): SubscriptionState {
      return SubscriptionState(
        isSubscribed = true,
        planName = planName,
        renewalDate = renewalDate,
      )
    }
  }
}

class SubscriptionReducer(
  private val state: SubscriptionState,
) {
  fun onCancelRequested(): SubscriptionState {
    if (!state.isSubscribed || state.isCancelling) {
      return state
    }
    return state.copy(isCancelling = true)
  }

  fun onCancelCompleted(): SubscriptionState {
    return SubscriptionState.free()
  }
}
