package preview_screen.auth // แก้ package ให้ตรงกับไฟล์

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.wealthvault.introduction.ui.IntroContent
import com.wealthvault.introduction.ui.IntroQuestionContent
import com.wealthvault.register.ui.RegisterContent

@Preview(showBackground = true, name = "Preview")
@Composable
fun IntroScreenPreview() {
//    IntroContent(
//        onBackClick = {},
//        onFinish = {},
//    )

    IntroQuestionContent(
        firstName = "",
        onFirstNameChange = {},
        lastName = "",
        onLastNameChange = {},
        phoneNum = "",
        onPhoneNumChange = {},
        dob = "",
        onDobChange = {},
        onBackClick = {},
        onNextClick = {},
    )
}

