package com.example.androidtemplate.core.ui

sealed interface UiState<out T> {
  data object Idle : UiState<Nothing>
  data object Loading : UiState<Nothing>
  data class Success<T>(val data: T) : UiState<T>
  data class Error(val message: String) : UiState<Nothing>
}

sealed interface UiEvent {
  data class ShowSnackbar(val message: String) : UiEvent
  data class Navigate(val route: String) : UiEvent
  data class OpenSheet(val id: String) : UiEvent
  data class DismissSheet(val id: String) : UiEvent
}
