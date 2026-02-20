package com.example.androidtemplate.features.mypage

enum class MyPageDialogId {
  Logout,
  DeleteStep1,
  DeleteStep2,
}

fun highestPriorityDialog(
  isLogoutDialogVisible: Boolean,
  isDeleteStep1DialogVisible: Boolean,
  isDeleteStep2DialogVisible: Boolean,
): MyPageDialogId? {
  return when {
    isDeleteStep2DialogVisible -> MyPageDialogId.DeleteStep2
    isDeleteStep1DialogVisible -> MyPageDialogId.DeleteStep1
    isLogoutDialogVisible -> MyPageDialogId.Logout
    else -> null
  }
}
