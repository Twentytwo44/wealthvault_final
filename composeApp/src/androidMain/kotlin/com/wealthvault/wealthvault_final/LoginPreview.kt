package com.wealthvault.wealthvault_final // แก้ package ให้ตรงกับไฟล์

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.login.ui.LoginContent // เรียกใช้ UI จาก Module login

@Preview(showBackground = true, name = "Preview")
@Composable
fun LoginScreenPreview() {
    LoginContent(
        username = "",
        onUsernameChange = {},
        password = "",
        onPasswordChange = {},
        isLoading = false,
        onLoginClick = {}
    )
}
//fun MainScreenPreview(){
//    MainScreen()
//}
