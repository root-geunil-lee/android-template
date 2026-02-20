package com.example.androidtemplate.ui

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.example.androidtemplate.features.auth.AuthMethodsScreen
import com.example.androidtemplate.features.auth.EmailSignInScreen
import com.example.androidtemplate.features.auth.OtpFlowState
import com.example.androidtemplate.features.auth.OtpVerifyScreen
import com.example.androidtemplate.features.auth.OAuthFlowState
import com.example.androidtemplate.features.home.HomeScreen
import com.example.androidtemplate.features.mypage.MyPageScreen
import com.example.androidtemplate.features.mypage.SubscriptionScreen
import androidx.compose.ui.unit.dp
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
        onViewPlans = {},
      )
    }

    composeRule.onNodeWithText("Continue with Email →").assertIsDisplayed()
    composeRule.onAllNodesWithTag("email_input").assertCountEquals(0)
  }

  @Test
  fun authMethodsScreen_largeFontScale_keepsEmailActionReachable() {
    composeRule.setContent {
      CompositionLocalProvider(
        LocalDensity provides Density(density = 1f, fontScale = 2f),
      ) {
        AuthMethodsScreen(
          oauthState = OAuthFlowState.Idle,
          onApple = {},
          onGoogle = {},
          onKakao = {},
          onContinueWithEmail = {},
          onViewPlans = {},
        )
      }
    }

    composeRule.onNodeWithTag("auth_methods_scroll").performScrollToNode(hasText("Continue with Email →"))
    composeRule.onNodeWithText("Continue with Email →").assertIsDisplayed()
  }

  @Test
  fun authMethodsScreen_providerButtonsFollowRequiredOrderAndTouchTarget() {
    composeRule.setContent {
      AuthMethodsScreen(
        oauthState = OAuthFlowState.Idle,
        onApple = {},
        onGoogle = {},
        onKakao = {},
        onContinueWithEmail = {},
        onViewPlans = {},
      )
    }

    composeRule.onNodeWithTag("auth_provider_0_google").assertIsDisplayed().assertHeightIsAtLeast(56.dp)
    composeRule.onNodeWithTag("auth_provider_1_apple").assertIsDisplayed().assertHeightIsAtLeast(52.dp)
    composeRule.onNodeWithTag("auth_provider_2_kakao").assertIsDisplayed().assertHeightIsAtLeast(52.dp)
    composeRule.onNodeWithTag("auth_provider_3_email").assertIsDisplayed().assertHeightIsAtLeast(44.dp)
  }

  @Test
  fun authMethodsScreen_handlingCallbackDisablesProviderActions() {
    composeRule.setContent {
      AuthMethodsScreen(
        oauthState = OAuthFlowState.HandlingCallback,
        onApple = {},
        onGoogle = {},
        onKakao = {},
        onContinueWithEmail = {},
        onViewPlans = {},
      )
    }

    composeRule.onNodeWithTag("auth_provider_0_google").assertIsNotEnabled()
    composeRule.onNodeWithTag("auth_provider_1_apple").assertIsNotEnabled()
    composeRule.onNodeWithTag("auth_provider_2_kakao").assertIsNotEnabled()
    composeRule.onNodeWithTag("auth_provider_3_email").assertIsNotEnabled()
  }

  @Test
  fun authMethodsScreen_talkBackLabelsMatchButtonText() {
    composeRule.setContent {
      AuthMethodsScreen(
        oauthState = OAuthFlowState.Idle,
        onApple = {},
        onGoogle = {},
        onKakao = {},
        onContinueWithEmail = {},
        onViewPlans = {},
      )
    }

    composeRule.onNodeWithContentDescription("Continue with Google").assertHasClickAction()
    composeRule.onNodeWithContentDescription("Sign in with Apple").assertHasClickAction()
    composeRule.onNodeWithContentDescription("Continue with Kakao").assertHasClickAction()
    composeRule.onNodeWithContentDescription("Continue with Email").assertHasClickAction()
    composeRule.onNodeWithContentDescription("View plans").assertHasClickAction()
  }

  @Test
  fun emailSignInScreen_matchesParityCopyAndValidation() {
    composeRule.setContent {
      EmailSignInScreen(
        state = OtpFlowState.Idle,
        onBack = {},
        onSendCode = {},
      )
    }

    composeRule.onNodeWithText("Sign in").assertIsDisplayed()
    composeRule.onNodeWithText("Enter your email to receive a one-time verification code.").assertIsDisplayed()
    composeRule.onNodeWithText("Email").assertIsDisplayed()
    composeRule.onNodeWithTag("email_send_cta").assertIsNotEnabled()

    composeRule.onNodeWithTag("email_input").performTextInput("user@example.com")
    composeRule.onNodeWithTag("email_send_cta").assertIsEnabled()
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

    composeRule.onNodeWithTag("otp_verify_button").assertIsEnabled()
  }

  @Test
  fun otpVerifyScreen_largeFontScale_keepsChangeEmailActionReachable() {
    composeRule.setContent {
      CompositionLocalProvider(
        LocalDensity provides Density(density = 1f, fontScale = 2f),
      ) {
        OtpVerifyScreen(
          email = "user@example.com",
          state = OtpFlowState.Idle,
          onBack = {},
          onResendCode = {},
          onVerifyCode = {},
        )
      }
    }

    composeRule.onNodeWithTag("otp_verify_scroll").performScrollToNode(hasText("Change email"))
    composeRule.onNodeWithText("Change email").assertIsDisplayed()
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
  fun otpVerifyScreen_matchesParityCopy() {
    composeRule.setContent {
      OtpVerifyScreen(
        email = "user@example.com",
        state = OtpFlowState.SentCode(email = "user@example.com", cooldownSeconds = 60),
        onBack = {},
        onResendCode = {},
        onVerifyCode = {},
      )
    }

    composeRule.onAllNodesWithText("Verify").assertCountEquals(2)
    composeRule.onNodeWithText("Enter verification code").assertIsDisplayed()
    composeRule.onNodeWithText("Code expires in 10 minutes.").assertIsDisplayed()
    composeRule.onNodeWithTag("otp_subtext").assertTextContains("We sent a 6-digit code to user@example.com")
    composeRule.onNodeWithText("Resend code").assertIsDisplayed()
    composeRule.onNodeWithText("Change email").assertIsDisplayed()
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

  @Test
  fun homeScreen_matchesRequiredInformationArchitecture() {
    composeRule.setContent {
      HomeScreen(
        onOpenPaywall = {},
        paywallResultMessage = null,
      )
    }

    composeRule.onNodeWithText("Home").assertIsDisplayed()
    composeRule.onNodeWithText("Upgrade").assertIsDisplayed().assertHasClickAction()
    composeRule.onNodeWithText("Core Features").assertIsDisplayed()
    composeRule.onNodeWithText("Feed").assertIsDisplayed()
    composeRule.onNodeWithText("Chat").assertIsDisplayed()
    composeRule.onNodeWithText("Tools").assertIsDisplayed()
    composeRule.onNodeWithText("Tasks").assertIsDisplayed()
    composeRule.onAllNodesWithText("Template module").assertCountEquals(4)
  }

  @Test
  fun myPageScreen_rowsMeetMinimumTouchTargetHeight() {
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

    composeRule.onNodeWithTag("row_item_Subscription").assertHeightIsAtLeast(48.dp)
    composeRule.onNodeWithTag("row_item_Delete_Account").assertHeightIsAtLeast(48.dp)
  }

  @Test
  fun myPageScreen_rowsExposeTalkBackLabelAndClickAction() {
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

    composeRule.onNodeWithContentDescription("Subscription").assertHasClickAction()
    composeRule.onNodeWithContentDescription("Delete Account").assertHasClickAction()
  }

  @Test
  fun subscriptionScreen_showsManageActions() {
    composeRule.setContent {
      SubscriptionScreen(
        onBack = {},
        onOpenPlanSelection = {},
        onOpenPaymentMethod = {},
        onOpenStoreSubscription = {},
      )
    }

    composeRule.onNodeWithText("Change Plan").assertIsDisplayed()
    composeRule.onNodeWithText("Payment Method").assertIsDisplayed()
    composeRule.onNodeWithText("Manage in App Store").assertIsDisplayed()
    composeRule.onNodeWithText("Cancel Subscription").assertIsDisplayed()
  }

  @Test
  fun subscriptionScreen_largeFontScale_keepsBackActionReachable() {
    composeRule.setContent {
      CompositionLocalProvider(
        LocalDensity provides Density(density = 1f, fontScale = 2f),
      ) {
        SubscriptionScreen(
          onBack = {},
          onOpenPlanSelection = {},
          onOpenPaymentMethod = {},
          onOpenStoreSubscription = {},
        )
      }
    }

    composeRule.onNodeWithTag("subscription_scroll").performScrollToNode(hasText("Subscriptions are managed through your Apple ID settings."))
    composeRule.onNodeWithText("Subscriptions are managed through your Apple ID settings.").assertIsDisplayed()
  }
}
