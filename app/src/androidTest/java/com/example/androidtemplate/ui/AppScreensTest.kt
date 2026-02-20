package com.example.androidtemplate.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.androidtemplate.features.auth.AuthMethodsScreen
import com.example.androidtemplate.features.auth.OAuthFlowState
import com.example.androidtemplate.features.mypage.MyPageScreen
import org.junit.Rule
import org.junit.Test

class AppScreensTest {

  @get:Rule
  val composeRule = createComposeRule()

  @Test
  fun authMethodsScreen_hasNoInputField() {
    composeRule.setContent {
      AuthMethodsScreen(
        oauthState = OAuthFlowState.Idle,
        onApple = {},
        onGoogle = {},
        onKakao = {},
        onContinueWithEmail = {},
      )
    }

    composeRule.onNodeWithText("Continue with Email").assertIsDisplayed()
    composeRule.onAllNodesWithTag("email_input").assertCountEquals(0)
  }

  @Test
  fun myPageScreen_matchesRequiredInformationArchitecture() {
    composeRule.setContent {
      MyPageScreen(
        onEditProfile = {},
        onSubscription = {},
        onPurchaseHistory = {},
        onTerms = {},
        onPrivacy = {},
        onLogout = {},
        onDeleteAccount = {},
      )
    }

    composeRule.onNodeWithText("Profile").assertIsDisplayed()
    composeRule.onNodeWithText("Settings").assertIsDisplayed()
    composeRule.onNodeWithText("Support").assertIsDisplayed()
    composeRule.onNodeWithText("Danger Zone").assertIsDisplayed()
    composeRule.onNodeWithText("Subscription").assertIsDisplayed()
    composeRule.onNodeWithText("Purchase History").assertIsDisplayed()
    composeRule.onNodeWithText("Log out").assertIsDisplayed()
    composeRule.onNodeWithText("Delete Account").assertIsDisplayed()
  }
}
