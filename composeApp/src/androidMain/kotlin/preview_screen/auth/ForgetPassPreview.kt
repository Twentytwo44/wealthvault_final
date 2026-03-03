package preview_screen.auth // แก้ package ให้ตรงกับไฟล์

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.fogetpassword.ui.ConfirmOTPContent
import com.wealthvault.fogetpassword.ui.ResetPasswordContent
import com.wealthvault.forgetpassword.ui.ForgetPasswordContent


@Preview(showBackground = true, name = "Preview")
@Composable

fun ForgetPasswordScreenPreview() {
    ForgetPasswordContent(
        email = "",
        onEmailChange = {},
        onBackClick = {},
        onSendOtpClick = {}
    )
    ConfirmOTPContent(
        emailSentTo = "",
        otpCode = "",
        onOtpChange = {},
        onBackClick = {},
        onVerifyClick = {},
        onResendClick = {},
    )
    ResetPasswordContent(
        newPassword = "",
        onNewPasswordChange = {},
        confirmPassword = "",
        onConfirmPasswordChange = {},
        onBackClick = {},
        onSubmitClick = {},
    )

}




