package preview_screen.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.forgetpassword.ui.ForgetPasswordScreen

// นำเข้า (Import) หน้าจอต่างๆ ให้ครบ
import com.wealthvault.login.ui.LoginContent
import com.wealthvault.register.ui.RegisterContent
import com.wealthvault.register.ui.RegisterScreen

// import ...MainScreen (ถ้ามีหน้า MainScreen แยกต่างหาก อย่าลืม import มานะครับ)

// หน้าที่ 1: เข้าสู่ระบบ (Login)
@Preview(showBackground = true, name = "1. Login Screen")
@Composable
fun LoginScreenPreview() {
    LoginContent(
        username = "", // 👈 แก้จาก username: String เป็นการส่งค่าจำลองแทน
        onUsernameChange = {},
        password = "",
        onPasswordChange = {},
        isLoading = false, // 👈 แก้จาก isLoading: Boolean
        onLoginClick = {},
        onGoogleClick = {},
        onForgotPasswordClick = {},
        onRegisterClick = {}
    )
}

// หน้าที่ 2: สมัครสมาชิก (Register)
@Preview(showBackground = true, name = "2. Register Screen")
@Composable
fun RegisterScreenPreview() {
    RegisterContent(
        username = "",
        onUsernameChange = {},
        password = "",
        onPasswordChange = {},
        confirmPassword = "",
        onConfirmPasswordChange = {},
        isLoading = false,
        onRegisterClick = {},
        onLoginClick = {},
        onGoogleClick = {}
    )
}

