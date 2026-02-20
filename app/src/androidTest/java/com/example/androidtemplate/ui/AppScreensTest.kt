package com.example.androidtemplate.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.androidtemplate.features.auth.AuthMethodsScreen
import com.example.androidtemplate.features.auth.OtpFlowState
import com.example.androidtemplate.features.auth.OtpVerifyScreen
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
  fun otpVerifyScreen_rendersSixAccessibleSlots_andAcceptsPastedDigitsOnly() {
    composeRule.setContent {
      OtpVerifyScreen(
        email = "user@example.com",
        state = OtpFlowState.Idle,
        onBack = {},
        onResendCode = {},
        onVerifyCode = {},
      )
    }

    composeRule.onNodeWithTag("otp_hidden_input").performClick()
    composeRule.onNodeWithTag("otp_hidden_input").performTextInput("12ab34cd56")

    (0 until 6).forEach { index ->
      composeRule.onNodeWithTag("otp_digit_slot_$index").assertIsDisplayed()
      composeRule.onNodeWithContentDescription("digit ${index + 1} of 6").assertIsDisplayed()
    }

    composeRule.onNodeWithText("Verify").assertIsEnabled()
  }

  @Test
  fun otpVerifyScreen_showsResendCooldownMessage() {
    composeRule.setContent {
      OtpVerifyScreen(
        email = "user@example.com",
        state = OtpFlowState.SentCode(email = "user@example.com", cooldownSeconds = 60),
        onBack = {},
        onResendCode = {},
        onVerifyCode = {},
      )
    }

    composeRule.onNodeWithText("Resend in 01:00").assertIsDisplayed()
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
