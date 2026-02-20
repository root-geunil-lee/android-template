package com.example.androidtemplate.features.mypage

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class EditProfileFormTest {

  @Test
  fun initialState_hasReadonlyEmailAndDisabledSave() {
    val state = EditProfileState.initial(email = "user@example.com")

    assertThat(state.email).isEqualTo("user@example.com")
    assertThat(state.isEmailEditable).isFalse()
    assertThat(state.isSaveEnabled).isFalse()
  }

  @Test
  fun updateDisplayName_withValidValue_enablesSave() {
    val reducer = EditProfileReducer(EditProfileState.initial(email = "user@example.com"))

    val updated = reducer.onDisplayNameChanged("Jane Doe")

    assertThat(updated.displayName).isEqualTo("Jane Doe")
    assertThat(updated.validationError).isNull()
    assertThat(updated.isSaveEnabled).isTrue()
  }

  @Test
  fun updateDisplayName_withBlankValue_showsValidationErrorAndDisablesSave() {
    val reducer = EditProfileReducer(EditProfileState.initial(email = "user@example.com"))

    val updated = reducer.onDisplayNameChanged("   ")

    assertThat(updated.validationError).isEqualTo("Display name is required")
    assertThat(updated.isSaveEnabled).isFalse()
  }
}
