package com.example.androidtemplate

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppStateViewModel : ViewModel() {
  private val _isAuthenticated = MutableStateFlow(false)
  val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

  fun onAuthenticated() {
    _isAuthenticated.value = true
  }

  fun onLoggedOut() {
    _isAuthenticated.value = false
  }
}
