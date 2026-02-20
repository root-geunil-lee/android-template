package com.example.androidtemplate.features.mypage

data class EditProfileState(
  val displayName: String,
  val email: String,
  val isEmailEditable: Boolean,
  val validationError: String?,
  val isSaveEnabled: Boolean,
) {
  companion object {
    fun initial(email: String): EditProfileState {
      return EditProfileState(
        displayName = "",
        email = email,
        isEmailEditable = false,
        validationError = null,
        isSaveEnabled = false,
      )
    }
  }
}

class EditProfileReducer(
  private val currentState: EditProfileState,
) {
  fun onDisplayNameChanged(rawValue: String): EditProfileState {
    val nextDisplayName = rawValue.trim()
    val isValid = nextDisplayName.isNotBlank()

    return currentState.copy(
      displayName = nextDisplayName,
      validationError = if (isValid) null else "Display name is required",
      isSaveEnabled = isValid,
    )
  }
}
