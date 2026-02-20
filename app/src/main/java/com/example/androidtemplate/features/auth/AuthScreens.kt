package com.example.androidtemplate.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.androidtemplate.ui.theme.IosPalette
import kotlinx.coroutines.delay

@Composable
fun AuthMethodsScreen(
  oauthState: OAuthFlowState,
  onApple: () -> Unit,
  onGoogle: () -> Unit,
  onKakao: () -> Unit,
  onContinueWithEmail: () -> Unit,
  onViewPlans: () -> Unit,
) {
  val isBusy = oauthState == OAuthFlowState.HandlingCallback
  val oauthMessage = when (oauthState) {
    is OAuthFlowState.Error -> oauthState.message
    OAuthFlowState.HandlingCallback -> "Signing in..."
    else -> null
  }

  Scaffold(
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .testTag("auth_methods_scroll"),
      verticalArrangement = Arrangement.Top,
      horizontalAlignment = Alignment.Start,
    ) {
      Spacer(Modifier.height(80.dp))
      Text(
        text = "Sign in",
        style = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.Label,
      )
      Spacer(Modifier.height(40.dp))

      // iOS-first layout with Android exception: Google remains the first entry point.
      PrimaryProviderAction(
        text = "Continue with Google",
        monogram = "G",
        enabled = !isBusy,
        tag = "auth_provider_0_google",
        onClick = onGoogle,
      )

      Spacer(Modifier.height(12.dp))

      ProviderRowsContainer(
        first = ProviderRowSpec(
          text = "Sign in with Apple",
          monogram = "A",
          tag = "auth_provider_1_apple",
          onClick = onApple,
          enabled = !isBusy,
        ),
        second = ProviderRowSpec(
          text = "Continue with Kakao",
          monogram = "K",
          tag = "auth_provider_2_kakao",
          onClick = onKakao,
          enabled = !isBusy,
        ),
      )

      Spacer(Modifier.height(20.dp))
      OrDivider()
      Spacer(Modifier.height(20.dp))

      TextButton(
        onClick = onContinueWithEmail,
        enabled = !isBusy,
        colors = ButtonDefaults.textButtonColors(
          contentColor = IosPalette.SystemBlue,
          disabledContentColor = IosPalette.SecondaryLabel,
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(min = 44.dp)
          .semantics { contentDescription = "Continue with Email" }
          .testTag("auth_provider_3_email"),
      ) {
        Text(
          text = "Continue with Email \u2192",
          style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium),
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Start,
        )
      }

      TextButton(
        onClick = onViewPlans,
        enabled = !isBusy,
        colors = ButtonDefaults.textButtonColors(
          contentColor = IosPalette.SecondaryLabel,
          disabledContentColor = IosPalette.SecondaryLabel.copy(alpha = 0.6f),
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
          .heightIn(min = 36.dp)
          .semantics { contentDescription = "View plans" },
      ) {
        Text(
          text = "View plans",
          style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Start,
        )
      }

      if (!oauthMessage.isNullOrBlank()) {
        Spacer(Modifier.height(16.dp))
        Text(
          text = oauthMessage,
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SecondaryLabel,
          modifier = Modifier.fillMaxWidth(),
        )
      }

      Spacer(Modifier.height(24.dp))
    }
  }
}

private data class ProviderRowSpec(
  val text: String,
  val monogram: String,
  val tag: String,
  val enabled: Boolean,
  val onClick: () -> Unit,
)

@Composable
private fun PrimaryProviderAction(
  text: String,
  monogram: String,
  enabled: Boolean,
  tag: String,
  onClick: () -> Unit,
) {
  Button(
    onClick = onClick,
    enabled = enabled,
    shape = RoundedCornerShape(15.dp),
    colors = ButtonDefaults.buttonColors(
      containerColor = Color.Black,
      contentColor = Color.White,
      disabledContainerColor = Color.Black.copy(alpha = 0.55f),
      disabledContentColor = Color.White.copy(alpha = 0.65f),
    ),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
    modifier = Modifier
      .fillMaxWidth()
      .height(56.dp)
      .semantics { contentDescription = text }
      .testTag(tag),
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
      Box(
        modifier = Modifier
          .size(22.dp)
          .background(Color.White.copy(alpha = 0.18f), CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = monogram,
          style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
        )
      }
      Text(
        text = text,
        style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
      )
    }
  }
}

@Composable
private fun ProviderRowsContainer(
  first: ProviderRowSpec,
  second: ProviderRowSpec,
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(14.dp))
      .border(
        width = 1.dp,
        color = IosPalette.Separator.copy(alpha = 0.22f),
        shape = RoundedCornerShape(14.dp),
      ),
  ) {
    ProviderRowButton(row = first)
    HairlineSeparator(modifier = Modifier.padding(start = 52.dp))
    ProviderRowButton(row = second)
  }
}

@Composable
private fun ProviderRowButton(
  row: ProviderRowSpec,
) {
  TextButton(
    onClick = row.onClick,
    enabled = row.enabled,
    colors = ButtonDefaults.textButtonColors(
      contentColor = IosPalette.Label,
      disabledContentColor = IosPalette.SecondaryLabel,
    ),
    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
    modifier = Modifier
      .fillMaxWidth()
      .height(52.dp)
      .semantics { contentDescription = row.text }
      .testTag(row.tag),
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      Box(
        modifier = Modifier
          .size(20.dp)
          .background(IosPalette.TertiarySystemFill, CircleShape),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = row.monogram,
          style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.SemiBold),
          color = IosPalette.Label,
        )
      }

      Text(
        text = row.text,
        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium),
        modifier = Modifier.weight(1f),
        textAlign = TextAlign.Start,
      )

      Icon(
        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
        contentDescription = null,
        tint = IosPalette.TertiaryLabel,
      )
    }
  }
}

@Composable
private fun OrDivider() {
  Row(
    modifier = Modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    HairlineSeparator(modifier = Modifier.weight(1f))
    Text(
      text = "or",
      style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal),
      color = IosPalette.SecondaryLabel,
    )
    HairlineSeparator(modifier = Modifier.weight(1f))
  }
}

@Composable
private fun HairlineSeparator(modifier: Modifier = Modifier) {
  Spacer(
    modifier = modifier
      .fillMaxWidth()
      .height(1.dp)
      .background(IosPalette.Separator.copy(alpha = 0.3f)),
  )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailSignInScreen(
  state: OtpFlowState,
  onBack: () -> Unit,
  onSendCode: (String) -> Unit,
) {
  var email by rememberSaveable { mutableStateOf("") }
  var hasSubmitted by rememberSaveable { mutableStateOf(false) }
  var didBlurInvalidEmail by rememberSaveable { mutableStateOf(false) }
  var emailFieldFocused by rememberSaveable { mutableStateOf(false) }

  val normalizedEmail = email.trim().lowercase()
  val isValidEmail = normalizedEmail.contains("@") && normalizedEmail.substringAfterLast('.', "").length >= 2
  val isSending = state == OtpFlowState.SendingCode
  val showEmailValidationError = normalizedEmail.isNotEmpty() && !isValidEmail && (hasSubmitted || didBlurInvalidEmail)
  val isPrimaryDisabled = !isValidEmail || isSending

  val helperMessage = when (state) {
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
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 24.dp),
    ) {
      Text(
        text = "Enter your email to receive a one-time verification code.",
        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal),
        color = IosPalette.SecondaryLabel,
      )
      Spacer(Modifier.height(24.dp))

      EmailField(
        value = email,
        onValueChange = { email = it },
        focused = emailFieldFocused,
        onFocusChanged = { focused ->
          if (emailFieldFocused && !focused && normalizedEmail.isNotEmpty() && !isValidEmail) {
            didBlurInvalidEmail = true
          }
          emailFieldFocused = focused
        },
        onClear = { email = "" },
        enabled = !isSending,
        isError = showEmailValidationError,
      )

      if (showEmailValidationError) {
        Text(
          text = "Enter a valid email address.",
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SystemRed,
          modifier = Modifier.padding(top = 8.dp, start = 20.dp),
        )
      }

      Button(
        onClick = {
          hasSubmitted = true
          if (isValidEmail && !isSending) {
            onSendCode(normalizedEmail)
          }
        },
        enabled = !isPrimaryDisabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.Black,
          contentColor = Color.White,
          disabledContainerColor = Color.Black.copy(alpha = 0.2f),
          disabledContentColor = Color.White.copy(alpha = 0.62f),
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp)
          .height(56.dp)
          .testTag("email_send_cta"),
      ) {
        if (isSending) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = Color.White,
            strokeWidth = 2.dp,
          )
          Spacer(Modifier.width(8.dp))
        }
        Text("Send verification code")
      }

      if (!helperMessage.isNullOrBlank()) {
        Text(
          text = helperMessage,
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SecondaryLabel,
          modifier = Modifier.padding(top = 8.dp, start = 20.dp),
        )
      }
    }
  }
}

@Composable
private fun EmailField(
  value: String,
  onValueChange: (String) -> Unit,
  focused: Boolean,
  onFocusChanged: (Boolean) -> Unit,
  onClear: () -> Unit,
  enabled: Boolean,
  isError: Boolean,
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(14.dp))
      .then(
        if (isError) {
          Modifier.border(1.5.dp, IosPalette.SystemRed.copy(alpha = 0.75f), RoundedCornerShape(14.dp))
        } else {
          Modifier
        },
      )
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    BasicTextField(
      value = value,
      onValueChange = onValueChange,
      enabled = enabled,
      singleLine = true,
      textStyle = TextStyle(fontSize = 17.sp, color = IosPalette.Label),
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Send,
      ),
      keyboardActions = KeyboardActions(
        onSend = {},
      ),
      decorationBox = { innerField ->
        Box {
          if (value.isBlank()) {
            Text(
              text = "Email",
              style = TextStyle(fontSize = 17.sp),
              color = IosPalette.SecondaryLabel,
            )
          }
          innerField()
        }
      },
      modifier = Modifier
        .weight(1f)
        .onFocusChanged { onFocusChanged(it.isFocused) }
        .testTag("email_input"),
    )

    if (focused && value.isNotBlank()) {
      IconButton(
        onClick = onClear,
        modifier = Modifier.size(20.dp),
      ) {
        Icon(
          imageVector = Icons.Filled.Close,
          contentDescription = "Clear email",
          tint = IosPalette.TertiaryLabel,
          modifier = Modifier.size(18.dp),
        )
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
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
      if (runCatching { focusRequester.requestFocus() }.isSuccess) {
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
    containerColor = IosPalette.SystemGroupedBackground,
  ) { innerPadding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp)
        .testTag("otp_verify_scroll"),
    ) {
      Spacer(Modifier.height(34.dp))
      Text(
        text = "Enter verification code",
        style = TextStyle(fontSize = 23.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.Label,
      )
      Spacer(Modifier.height(9.dp))
      Text(
        text = "We sent a 6-digit code to $email",
        style = TextStyle(fontSize = 15.sp),
        color = IosPalette.SecondaryLabel,
        modifier = Modifier.testTag("otp_subtext"),
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

      Spacer(Modifier.height(8.dp))
      Text(
        text = "Code expires in 10 minutes.",
        style = TextStyle(fontSize = 15.sp),
        color = IosPalette.SecondaryLabel,
      )

      if (!backendMessage.isNullOrBlank()) {
        Text(
          text = backendMessage,
          style = TextStyle(fontSize = 13.sp),
          color = IosPalette.SystemRed,
          modifier = Modifier.padding(top = 8.dp),
        )
      }

      Button(
        onClick = { onVerifyCode(otp) },
        enabled = otp.length == OTP_CODE_LENGTH && !isVerifying,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color.Black,
          contentColor = Color.White,
          disabledContainerColor = Color.Black.copy(alpha = 0.12f),
          disabledContentColor = Color.White.copy(alpha = 0.5f),
        ),
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 24.dp)
          .height(56.dp)
          .testTag("otp_verify_button"),
      ) {
        if (isVerifying) {
          CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            color = Color.White,
            strokeWidth = 2.dp,
          )
        } else {
          Text("Verify")
        }
      }

      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        TextButton(
          onClick = onResendCode,
          enabled = canResend,
          colors = ButtonDefaults.textButtonColors(
            contentColor = IosPalette.SystemBlue,
            disabledContentColor = IosPalette.SecondaryLabel,
          ),
          contentPadding = PaddingValues(0.dp),
          modifier = Modifier
            .heightIn(min = 44.dp)
            .testTag("otp_resend_button"),
        ) {
          Text(
            text = "Resend code",
            style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
          )
        }

        Spacer(Modifier.weight(1f))

        if (resendCooldownSeconds > 0) {
          Text(
            text = "Resend in ${formatCooldownMmSs(resendCooldownSeconds)}",
            style = TextStyle(fontSize = 13.sp),
            color = IosPalette.SecondaryLabel,
          )
        }
      }

      TextButton(
        onClick = onBack,
        enabled = !isVerifying,
        colors = ButtonDefaults.textButtonColors(
          contentColor = IosPalette.SystemBlue,
          disabledContentColor = IosPalette.SecondaryLabel,
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = Modifier
          .heightIn(min = 44.dp)
          .testTag("otp_change_email"),
      ) {
        Text(
          text = "Change email",
          style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium),
          modifier = Modifier.fillMaxWidth(),
          textAlign = TextAlign.Start,
        )
      }

      Spacer(Modifier.height(24.dp))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthFlowTopBar(
  title: String,
  onBack: (() -> Unit)?,
) {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = title,
        style = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
        color = IosPalette.Label,
      )
    },
    navigationIcon = {
      if (onBack != null) {
        IconButton(onClick = onBack) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(IosPalette.SecondarySystemGroupedBackground, CircleShape),
            contentAlignment = Alignment.Center,
          ) {
            Icon(
              imageVector = Icons.AutoMirrored.Filled.ArrowBack,
              contentDescription = "Navigate up",
              tint = IosPalette.Label,
            )
          }
        }
      }
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = IosPalette.SystemGroupedBackground,
      scrolledContainerColor = IosPalette.SystemGroupedBackground,
      titleContentColor = IosPalette.Label,
      navigationIconContentColor = IosPalette.Label,
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
      val isActiveSlot = isInputFocused && index == activeIndex && otp.length < OTP_CODE_LENGTH

      Box(
        modifier = Modifier
          .size(52.dp)
          .background(IosPalette.SecondarySystemGroupedBackground, RoundedCornerShape(13.dp))
          .border(
            width = if (isActiveSlot) 2.dp else 1.dp,
            color = if (isActiveSlot) IosPalette.SystemBlue else IosPalette.Separator.copy(alpha = 0.18f),
            shape = RoundedCornerShape(13.dp),
          )
          .clickable(enabled = isInputEnabled) { focusRequester.requestFocus() }
          .semantics { contentDescription = otpSlotDescription(index) }
          .testTag("otp_digit_slot_$index"),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = if (digit.isBlank()) " " else digit,
          style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.SemiBold),
          color = IosPalette.Label,
          textAlign = TextAlign.Center,
        )
      }
    }
  }
}
