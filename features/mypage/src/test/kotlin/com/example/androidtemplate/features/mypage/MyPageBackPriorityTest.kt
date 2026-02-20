package com.example.androidtemplate.features.mypage

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MyPageBackPriorityTest {

  @Test
  fun highestPriorityDialog_returnsDeleteStep2First() {
    val result = highestPriorityDialog(
      isLogoutDialogVisible = true,
      isDeleteStep1DialogVisible = true,
      isDeleteStep2DialogVisible = true,
    )

    assertThat(result).isEqualTo(MyPageDialogId.DeleteStep2)
  }

  @Test
  fun highestPriorityDialog_returnsDeleteStep1BeforeLogout() {
    val result = highestPriorityDialog(
      isLogoutDialogVisible = true,
      isDeleteStep1DialogVisible = true,
      isDeleteStep2DialogVisible = false,
    )

    assertThat(result).isEqualTo(MyPageDialogId.DeleteStep1)
  }

  @Test
  fun highestPriorityDialog_returnsNullWhenNoDialogVisible() {
    val result = highestPriorityDialog(
      isLogoutDialogVisible = false,
      isDeleteStep1DialogVisible = false,
      isDeleteStep2DialogVisible = false,
    )

    assertThat(result).isNull()
  }
}
