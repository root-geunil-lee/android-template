package com.example.androidtemplate.auth

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object AuthCallbackBus {
  private val _callbacks = MutableSharedFlow<String>(extraBufferCapacity = 1)
  val callbacks = _callbacks.asSharedFlow()

  fun emit(callbackUri: String) {
    _callbacks.tryEmit(callbackUri)
  }
}
