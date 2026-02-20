package com.example.androidtemplate

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppStateViewModelTest {

  @Test
  fun onAuthenticated_setsAuthenticatedTrue() {
    val viewModel = AppStateViewModel()

    viewModel.onAuthenticated()

    assertTrue(viewModel.isAuthenticated.value)
  }

  @Test
  fun onLoggedOut_setsAuthenticatedFalse() {
    val viewModel = AppStateViewModel()
    viewModel.onAuthenticated()

    viewModel.onLoggedOut()

    assertFalse(viewModel.isAuthenticated.value)
  }
}
