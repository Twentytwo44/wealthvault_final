package preview_screen.auth

// สังเกตว่า package ของคุณมีทั้ง fogetpassword และ forgetpassword ระวังพิมพ์ผิดตอนสร้างไฟล์จริงนะครับ
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.forgetpassword.ui.ConfirmOTPContent
import com.wealthvault.forgetpassword.ui.ForgetPasswordContent
import com.wealthvault.forgetpassword.ui.ResetPasswordContent

// หน้าที่ 1
//@Preview(showBackground = true, name = "1. Forget Password Screen")
//@Composable
//fun ForgetPasswordScreenPreview() {
//    ForgetPasswordContent(
//        email = "",
//        onEmailChange = {},
//        onBackClick = {},
//        onSendOtpClick = {}
//    )
//}
//
//// หน้าที่ 2
//@Preview(showBackground = true, name = "2. Confirm OTP Screen")
//@Composable
//fun ConfirmOTPScreenPreview() {
//    ConfirmOTPContent(
//        emailSentTo = "example@email.com", // แอบใส่ข้อมูลจำลองให้ Preview ดูสวยขึ้นครับ
//        otpCode = "",
//        onOtpChange = {},
//        onBackClick = {},
//        onVerifyClick = {},
//        onResendClick = {},
//    )
//}

// หน้าที่ 3
//@Preview(showBackground = true, name = "3. Reset Password Screen")
//@Composable
//fun ResetPasswordScreenPreview() {
//    ResetPasswordContent(
//        newPassword = "",
//        onNewPasswordChange = {},
//        confirmPassword = "",
//        onConfirmPasswordChange = {},
//        onBackClick = {},
//        onSubmitClick = {},
//    )
//}