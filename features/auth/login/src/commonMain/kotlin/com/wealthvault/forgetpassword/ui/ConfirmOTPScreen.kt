package com.wealthvault.forgetpassword.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.getScreenModel
import org.jetbrains.compose.resources.painterResource
import kotlinx.coroutines.delay

class ConfirmOTPScreen(private val email: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<ForgetPasswordScreenModel>()

        var otpCode by remember { mutableStateOf("") }

        val isLoading by screenModel.isLoading.collectAsState()
        val isOtpVerified by screenModel.isOtpVerified.collectAsState()
        val resetToken by screenModel.resetToken.collectAsState()
        val errorMessage by screenModel.errorMessage.collectAsState()

        LaunchedEffect(isOtpVerified) {
            if (isOtpVerified && resetToken.isNotEmpty()) {
                screenModel.clearFlags()
                navigator.push(ResetPasswordScreen(resetToken))
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ConfirmOTPContent(
                emailSentTo = email,
                otpCode = otpCode,
                errorMessage = errorMessage,
                onOtpChange = {
                    otpCode = it
                    screenModel.resetState()
                },
                onBackClick = { navigator.pop() },
                onVerifyClick = { screenModel.verifyOtp(email, otpCode) },
                onResendClick = { screenModel.sendOtp(email) }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)).clickable(enabled = false) {},
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = LightPrimary)
                }
            }
        }
    }
}

@Composable
fun ConfirmOTPContent(
    emailSentTo: String,
    otpCode: String,
    errorMessage: String?,
    onOtpChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
) {
    var timeLeft by remember { mutableStateOf(60) }
    var isResendEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(timeLeft) {
        if (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        } else {
            isResendEnabled = true
        }
    }

    WavyBackground {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding().padding(24.dp)) {
            Icon(painter = painterResource(Res.drawable.ic_common_back), contentDescription = "Back", tint = LightPrimary, modifier = Modifier.size(24.dp).clickable { onBackClick() })
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "ยืนยันรหัส OTP", color = LightPrimary, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "เราได้ส่งรหัส 6 หลักไปที่\n$emailSentTo", color = LightMuted, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(horizontal = 8.dp))
            Spacer(modifier = Modifier.height(40.dp))

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = RedErr,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    textAlign = TextAlign.End
                )
            }

            // 🌟 พระเอกของเรา: ชุด Component กรอกรหัส 6 ช่อง
            OtpInputField(
                otpLength = 6,
                otpValue = otpCode,
                onOtpValueChange = onOtpChange,
                isError = errorMessage != null,
                onComplete = {
                    // ถ้าพิมพ์ครบ 6 ตัวแล้ว ให้เรียกคำสั่งกดยืนยันให้อัตโนมัติก็ได้ (แล้วแต่ดีไซน์ ถ้าไม่ชอบเอาออกได้ครับ)
                    // onVerifyClick()
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = onVerifyClick, modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(percent = 30),
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary)
            ) {
                Text("ยืนยัน", style = MaterialTheme.typography.bodyLarge, color = LightSurface)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "ไม่ได้รับรหัส? ", color = LightMuted, style = MaterialTheme.typography.bodyMedium)

                if (isResendEnabled) {
                    Text(
                        text = "ส่งรหัสอีกครั้ง",
                        color = LightPrimary,
                        style = MaterialTheme.typography.bodyMedium,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable {
                            onResendClick()
                            timeLeft = 60
                            isResendEnabled = false
                        }
                    )
                } else {
                    Text(text = "ส่งรหัสอีกครั้ง ($timeLeft วิ)", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

// 🌟 Component หลักสำหรับวาดกล่อง 6 ช่อง (อัปเดตแก้เคอร์เซอร์ลอย)
@Composable
fun OtpInputField(
    otpLength: Int,
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    isError: Boolean,
    onComplete: () -> Unit
) {
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until otpLength) {
            val char = otpValue.getOrNull(i)?.takeIf { it != ' ' }?.toString() ?: ""
            val isFocused = otpValue.length == i || (otpValue.length == otpLength && i == otpLength - 1)

            // 🌟 1. ย้ายการวาดกรอบ สี และขนาด มาไว้ที่ Box ด้านนอก
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .background(LightSurface, RoundedCornerShape(12.dp))
                    .border(
                        width = if (isFocused || isError) 2.dp else 1.dp,
                        color = when {
                            isError -> RedErr
                            isFocused -> LightPrimary
                            char.isNotEmpty() -> LightPrimary.copy(alpha = 0.5f)
                            else -> LightBorder
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .height(50.dp)
                    .clickable { focusRequesters[i].requestFocus() }, // กดที่กรอบตรงไหนก็ได้เพื่อให้พิมพ์ต่อ
                contentAlignment = Alignment.Center // 🌟 2. สั่งให้ทุกอย่างในกล่องอยู่กึ่งกลางเป๊ะ
            ) {
                // 🌟 3. ปล่อยให้ BasicTextField มีขนาดพอดีกับตัวหนังสือ
                BasicTextField(
                    value = char,
                    onValueChange = { newValue ->
                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                            val chars = otpValue.padEnd(otpLength, ' ').toCharArray()
                            if (newValue.isNotEmpty()) {
                                chars[i] = newValue[0]
                                val newOtp = chars.concatToString().trimEnd()
                                onOtpValueChange(newOtp)
                                if (i < otpLength - 1) {
                                    focusRequesters[i + 1].requestFocus()
                                } else if (newOtp.length == otpLength && !newOtp.contains(' ')) {
                                    onComplete()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .focusRequester(focusRequesters[i])
                        .onKeyEvent { keyEvent ->
                            if (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.Backspace) {
                                val chars = otpValue.padEnd(otpLength, ' ').toCharArray()
                                if (char.isEmpty() && i > 0) {
                                    chars[i - 1] = ' '
                                    onOtpValueChange(chars.concatToString().trimEnd())
                                    focusRequesters[i - 1].requestFocus()
                                } else if (char.isNotEmpty()) {
                                    chars[i] = ' '
                                    onOtpValueChange(chars.concatToString().trimEnd())
                                }
                                true
                            } else {
                                false
                            }
                        },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.NumberPassword,
                        imeAction = if (i == otpLength - 1) ImeAction.Done else ImeAction.Next
                    ),
                    keyboardActions = KeyboardActions(
                        onNext = { if (i < otpLength - 1) focusRequesters[i + 1].requestFocus() },
                        onDone = { onComplete() }
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightPrimary
                    )
                    // เอา decorationBox ออกไปได้เลยเพราะไม่จำเป็นแล้ว
                )
            }
        }
    }
}