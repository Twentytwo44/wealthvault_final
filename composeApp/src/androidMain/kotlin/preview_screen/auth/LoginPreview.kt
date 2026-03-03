package preview_screen.auth // แก้ package ให้ตรงกับไฟล์

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.login.ui.LoginContent

@Preview(showBackground = true, name = "Preview")
@Composable
fun LoginScreenPreview() {
    LoginContent(
        username = "",
        onUsernameChange = {},
        password = "",
        onPasswordChange = {},
        isLoading = false,
        onLoginClick = {},
        onGoogleClick = {}
    )
}
//fun MainScreenPreview(){
//    MainScreen()
//}


