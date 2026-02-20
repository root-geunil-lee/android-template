package com.example.androidtemplate.features.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun AuthMethodsScreen(
  oauthState: OAuthFlowState,
  onApple: () -> Unit,
  onGoogle: () -> Unit,
  onKakao: () -> Unit,
  onContinueWithEmail: () -> Unit,
) {
  val oauthMessage = when (oauthState) {
    is OAuthFlowState.Error -> oauthState.message
    OAuthFlowState.HandlingCallback -> "Signing in..."
    else -> null
  }
  val isBusy = oauthState == OAuthFlowState.HandlingCallback

  Column(
    modifier = Modifier
      .fillMaxSize()
      .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
      .verticalScroll(rememberScrollState())
      // Auth graph host already applies status bar insets; keep +32dp breathing room per spec.
      .padding(top = 32.dp)
      .testTag("auth_methods_scroll"),
    verticalArrangement = Arrangement.Top,
  ) {
    Text("Sign in", style = MaterialTheme.typography.headlineMedium)
    if (!oauthMessage.isNullOrBlank()) {
      Spacer(Modifier.height(8.dp))
      Text(
        text = oauthMessage,
        color = MaterialTheme.colorScheme.error,
      )
    }
    Spacer(Modifier.height(24.dp))
    // Keep iOS parity for copy/entry points while using Android-native M3 hierarchy.
    // Google is the single primary action, Apple is secondary outline, Kakao is tertiary tonal,
    // and Email stays lightweight as text action.
    Column(
      verticalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.fillMaxWidth(),
    ) {
      Button(
        onClick = onGoogle,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 1.dp,
          pressedElevation = 1.dp,
          focusedElevation = 1.dp,
          hoveredElevation = 1.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Google" }
          .testTag("auth_provider_0_google"),
      ) {
        AuthProviderLabel(
          badge = "G",
          text = "Continue with Google",
          badgeContainerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f),
          badgeContentColor = MaterialTheme.colorScheme.onPrimary,
        )
      }

      OutlinedButton(
        onClick = onApple,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
          hoveredElevation = 0.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Apple" }
          .testTag("auth_provider_1_apple"),
      ) {
        AuthProviderLabel(
          badge = "A",
          text = "Continue with Apple",
          badgeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          badgeContentColor = MaterialTheme.colorScheme.onSurface,
        )
      }

      FilledTonalButton(
        onClick = onKakao,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
          hoveredElevation = 0.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.filledTonalButtonColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant,
          contentColor = MaterialTheme.colorScheme.onSurface,
          disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Kakao" }
          .testTag("auth_provider_2_kakao"),
      ) {
        AuthProviderLabel(
          badge = "K",
          text = "Continue with Kakao",
          badgeContainerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
          badgeContentColor = MaterialTheme.colorScheme.onSurface,
        )
      }

      TextButton(
        onClick = onContinueWithEmail,
        enabled = !isBusy,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 0.dp,
          pressedElevation = 0.dp,
          focusedElevation = 0.dp,
          hoveredElevation = 0.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.textButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .semantics { contentDescription = "Continue with Email" }
          .testTag("auth_provider_3_email"),
      ) {
        AuthProviderLabel(
          badge = "@",
          text = "Continue with Email",
          badgeContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          badgeContentColor = MaterialTheme.colorScheme.onSurface,
        )
      }
    }
  }
}

@Composable
private fun AuthProviderLabel(
  badge: String,
  text: String,
  badgeContainerColor: androidx.compose.ui.graphics.Color,
  badgeContentColor: androidx.compose.ui.graphics.Color,
) {
  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(12.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Box(
      modifier = Modifier
        .size(24.dp)
        .background(color = badgeContainerColor, shape = RoundedCornerShape(12.dp)),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = badge,
        color = badgeContentColor,
        style = MaterialTheme.typography.labelSmall,
      )
    }
    Text(
      text = text,
      style = MaterialTheme.typography.bodyLarge,
      modifier = Modifier.weight(1f),
      textAlign = TextAlign.Start,
    )
  }
}

@Composable
/*
PR checklist:
- [x] Copy parity checked
- [x] Insets/edge-to-edge checked
- [x] No dynamic color
- [x] No bottom Back CTA
- [x] OTP behaviors (paste, auto-advance, auto-verify) verified
*/
@OptIn(ExperimentalMaterial3Api::class)
fun EmailSignInScreen(
  state: OtpFlowState,
  onBack: () -> Unit,
  onSendCode: (String) -> Unit,
) {
  var email by rememberSaveable { mutableStateOf("") }
  val isValid = email.contains("@") && email.contains(".")
  val isSending = state == OtpFlowState.SendingCode

  val backendMessage = when (state) {
    is OtpFlowState.Error -> state.message
    is OtpFlowState.RateLimited -> "Too many requests. Try again in ${state.retryAfterSeconds}s"
    else -> null
  }

  Scaffold(
    topBar = {
      AuthFlowTopBar(
        title = "Sign in",
        onBack = onBack,
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
        .verticalScroll(rememberScrollState())
        .padding(top = 24.dp)
        .testTag("email_sign_in_scroll"),
      verticalArrangement = Arrangement.Top,
    ) {
      Text(
        text = "Enter your email to receive a one-time verification code.",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(Modifier.height(16.dp))
      OutlinedTextField(
        value = email,
        onValueChange = { email = it.trim() },
        label = { Text("Email") },
        placeholder = { Text("Email") },
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("email_input"),
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Email,
          imeAction = ImeAction.Send,
        ),
        keyboardActions = KeyboardActions(
          onSend = {
            if (isValid && !isSending) {
              onSendCode(email)
            }
          },
        ),
        isError = (email.isNotEmpty() && !isValid) || backendMessage != null,
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline,
          focusedLabelColor = MaterialTheme.colorScheme.onSurface,
          unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
          focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
          unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        supportingText = {
          when {
            email.isNotEmpty() && !isValid -> Text("Please enter a valid email")
            backendMessage != null -> Text(backendMessage)
          }
        },
      )

      Spacer(Modifier.height(24.dp))
      Button(
        onClick = { onSendCode(email) },
        enabled = isValid && !isSending,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 1.dp,
          pressedElevation = 1.dp,
          focusedElevation = 1.dp,
          hoveredElevation = 1.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .testTag("email_send_cta"),
      ) {
        if (isSending) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.dp,
          )
          Spacer(Modifier.width(8.dp))
        }
        Text("Send verification code")
      }
    }
  }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun OtpVerifyScreen(
  email: String,
  state: OtpFlowState,
  onBack: () -> Unit,
  onResendCode: () -> Unit,
  onVerifyCode: (String) -> Unit,
) {
  var otp by rememberSaveable { mutableStateOf("") }
  var lastAutoSubmittedCode by rememberSaveable { mutableStateOf("") }
  var resendCooldownSeconds by rememberSaveable(email) { mutableStateOf(0) }
  var otpFieldHasFocus by rememberSaveable { mutableStateOf(false) }
  val focusRequester = remember { FocusRequester() }
  val isVerifying = state == OtpFlowState.VerifyingCode

  val backendMessage = when (state) {
    is OtpFlowState.Error -> state.message
    is OtpFlowState.RateLimited -> "Too many attempts. Try again in ${state.retryAfterSeconds}s"
    else -> null
  }

  LaunchedEffect(Unit) {
    repeat(5) {
      val focused = runCatching {
        focusRequester.requestFocus()
      }.isSuccess
      if (focused) {
        return@LaunchedEffect
      }
      delay(16)
    }
  }

  LaunchedEffect(state, email) {
    when (state) {
      is OtpFlowState.SentCode -> {
        if (state.email == email) {
          resendCooldownSeconds = state.cooldownSeconds
        }
      }
      is OtpFlowState.RateLimited -> {
        resendCooldownSeconds = maxOf(resendCooldownSeconds, state.retryAfterSeconds)
      }
      else -> Unit
    }
  }

  LaunchedEffect(resendCooldownSeconds) {
    if (resendCooldownSeconds > 0) {
      delay(1000)
      resendCooldownSeconds -= 1
    }
  }

  LaunchedEffect(otp, state) {
    if (otp.length == OTP_CODE_LENGTH && otp != lastAutoSubmittedCode && !isVerifying) {
      lastAutoSubmittedCode = otp
      onVerifyCode(otp)
    }
  }

  val canResend = resendCooldownSeconds == 0 &&
    state != OtpFlowState.SendingCode &&
    !isVerifying

  Scaffold(
    topBar = {
      AuthFlowTopBar(
        title = "Verify",
        onBack = onBack,
      )
    },
    containerColor = MaterialTheme.colorScheme.background,
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
        .verticalScroll(rememberScrollState())
        .padding(top = 24.dp)
        .testTag("otp_verify_scroll"),
      verticalArrangement = Arrangement.Top,
    ) {
      Text(
        text = "Enter verification code",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "We sent a 6-digit code to $email",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.testTag("otp_subtext"),
      )
      Spacer(Modifier.height(8.dp))
      Text(
        text = "Code expires in 10 minutes.",
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
      )

      Spacer(Modifier.height(24.dp))
      BasicTextField(
        value = otp,
        onValueChange = { raw ->
          if (!isVerifying) {
            otp = sanitizeOtpInput(raw)
          }
        },
        enabled = !isVerifying,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(
          onDone = {
            if (otp.length == OTP_CODE_LENGTH && !isVerifying) {
              onVerifyCode(otp)
            }
          },
        ),
        modifier = Modifier
          .focusRequester(focusRequester)
          .onFocusChanged { focusState -> otpFieldHasFocus = focusState.isFocused }
          .size(1.dp)
          .alpha(0f)
          .testTag("otp_hidden_input"),
      )
      OtpDigitSlots(
        otp = otp,
        focusRequester = focusRequester,
        isInputEnabled = !isVerifying,
        isInputFocused = otpFieldHasFocus,
      )

      if (!backendMessage.isNullOrBlank()) {
        Spacer(Modifier.height(8.dp))
        Text(
          text = backendMessage,
          color = MaterialTheme.colorScheme.error,
          style = MaterialTheme.typography.bodyMedium,
        )
      }

      Spacer(Modifier.height(24.dp))
      Button(
        onClick = { onVerifyCode(otp) },
        enabled = otp.length == OTP_CODE_LENGTH && !isVerifying,
        shape = RoundedCornerShape(24.dp),
        elevation = ButtonDefaults.buttonElevation(
          defaultElevation = 1.dp,
          pressedElevation = 1.dp,
          focusedElevation = 1.dp,
          hoveredElevation = 1.dp,
          disabledElevation = 0.dp,
        ),
        colors = ButtonDefaults.buttonColors(
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = MaterialTheme.colorScheme.onPrimary,
          disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 56.dp)
          .testTag("otp_verify_button"),
      ) {
        if (isVerifying) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            strokeWidth = 2.dp,
          )
          Spacer(Modifier.width(8.dp))
        }
        Text("Verify")
      }

      Spacer(Modifier.height(16.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TextButton(
          onClick = onResendCode,
          enabled = canResend,
          colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
          contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
          modifier = Modifier
            .heightIn(min = 48.dp)
            .testTag("otp_resend_button"),
        ) {
          Text("Resend code")
        }
        if (resendCooldownSeconds > 0) {
          Text(
            text = "Resend in ${formatCooldownMmSs(resendCooldownSeconds)}",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
          )
        }
      }

      Spacer(Modifier.height(8.dp))
      TextButton(
        onClick = onBack,
        enabled = !isVerifying,
        colors = ButtonDefaults.textButtonColors(
          contentColor = MaterialTheme.colorScheme.onSurface,
          disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        ),
        contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp),
        modifier = Modifier
          .heightIn(min = 48.dp)
          .testTag("otp_change_email"),
      ) {
        Text("Change email")
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthFlowTopBar(
  title: String,
  onBack: () -> Unit,
) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
      )
    },
    navigationIcon = {
      IconButton(onClick = onBack) {
        Box(
          modifier = Modifier
            .size(36.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
          contentAlignment = Alignment.Center,
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Navigate up",
          )
        }
      }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.background,
      scrolledContainerColor = MaterialTheme.colorScheme.background,
      titleContentColor = MaterialTheme.colorScheme.onSurface,
      navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
    ),
  )
}

@Composable
private fun OtpDigitSlots(
  otp: String,
  focusRequester: FocusRequester,
  isInputEnabled: Boolean,
  isInputFocused: Boolean,
) {
  val activeIndex = when {
    otp.length >= OTP_CODE_LENGTH -> OTP_CODE_LENGTH - 1
    else -> otp.length
  }

  Row(
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    repeat(OTP_CODE_LENGTH) { index ->
      val digit = otp.getOrNull(index)?.toString().orEmpty()
      val isActiveSlot = isInputFocused && index == activeIndex
      Box(
        modifier = Modifier
          .size(52.dp)
          .border(
            width = 1.dp,
            color = when {
              isActiveSlot -> MaterialTheme.colorScheme.primary
              digit.isBlank() -> MaterialTheme.colorScheme.outline
              else -> MaterialTheme.colorScheme.primary
            },
            shape = RoundedCornerShape(12.dp),
          )
          .background(
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(12.dp),
          )
          .clickable(enabled = isInputEnabled, onClick = { focusRequester.requestFocus() })
          .semantics { contentDescription = otpSlotDescription(index) }
          .testTag("otp_digit_slot_$index"),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = if (digit.isBlank()) " " else digit,
          style = MaterialTheme.typography.titleLarge,
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}
