package com.wealthvault.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.login
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.theme.WvWaveGradientEnd
import com.wealthvault.core.theme.WvWaveGradientStart
import com.wealthvault.core.theme.WvBgGradientEnd
import com.wealthvault.core.theme.WvBgGradientStart
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.forgetpassword.ui.ForgetPasswordScreen
import com.wealthvault.core.navigation.SharedScreen
import org.jetbrains.compose.resources.painterResource

class LoginScreen() : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LoginScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()
        val registerScreen = rememberScreen(SharedScreen.Register)

        LoginContent(
            username = uiState.username,
            onUsernameChange = { screenModel.onAction(LoginUiAction.UsernameChanged(it)) },
            password = uiState.password,
            onPasswordChange = { screenModel.onAction(LoginUiAction.PasswordChanged(it)) },
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onLoginClick = {
                validateLoginInput(uiState.username, uiState.password)?.let { message ->
                    screenModel.onAction(LoginUiAction.ValidationFailed(message))
                } ?: run {
                    // SessionState is routed centrally by AppCoordinator.
                    // The callback remains for compatibility with older
                    // callers but this screen no longer owns global routing.
                    screenModel.onLoginClick()
                }
            },
            onGoogleClick = {
                screenModel.onGoogleClick()
            },
            onForgotPasswordClick = { navigator.push(ForgetPasswordScreen()) },
            onRegisterClick = { navigator.push(registerScreen) }
        )
    }
}

@Composable
fun LoginContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    WealthVaultTheme {
        WavyBackground {

            if (isLoading) {
                Dialog(onDismissRequest = {}) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = LightPrimary)
                    }
                }
            }

            // 🌟 แก้ไข: เพิ่ม verticalScroll และ padding เพื่อรองรับจอเล็กและคีย์บอร์ด
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()) // 🌟 ทำให้เลื่อนได้ถ้าเนื้อหาเกินจอ
                    .statusBarsPadding() // เว้นที่ให้แถบสถานะด้านบน
                    .imePadding() // 🌟 ดัน UI ขึ้นเมื่อคีย์บอร์ดเด้ง
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top // 🌟 เปลี่ยนเป็น Top เพื่อให้ scrolling ทำงานได้ถูกต้อง
            ) {

                Spacer(modifier = Modifier.height(20.dp)) // ระยะห่างด้านบนสุด

                // 🌟 แก้ไข: ลดขนาดชื่อแอป เปลี่ยน style เป็น headlineMedium พร้อมเพิ่มความหนาและถ่างช่องไฟ
                Text(
                    text = "Wealth & Vault",
                    color = LightPrimary,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp // 🌟 ถ่างช่องไฟเล็กน้อยให้ดูแพง
                    ),
                )


                // 🌟 ใส่รูปภาพประกอบหน้า Login
                Image(
                    painter = painterResource(Res.drawable.login),
                    contentDescription = "Login Illustration",
                    modifier = Modifier
                        .size(180.dp),
                    contentScale = ContentScale.Fit
                )

                LoginFields(
                    username = username,
                    onUsernameChange = onUsernameChange,
                    password = password,
                    onPasswordChange = onPasswordChange,
                    errorMessage = errorMessage,
                )

                LoginActions(
                    onLoginClick = onLoginClick,
                    onGoogleClick = onGoogleClick,
                    onForgotPasswordClick = onForgotPasswordClick,
                    onRegisterClick = onRegisterClick,
                )
            }
        }
    }
}

@Composable
fun WavyBackground(
    topWaveBrush: Brush = Brush.verticalGradient(colors = listOf(WvWaveGradientStart, WvWaveGradientEnd)),
    bottomBgBrush: Brush = Brush.verticalGradient(colors = listOf(WvBgGradientStart, WvBgGradientEnd)),
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bottomBgBrush)
            .drawBehind {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, size.height * 0.25f)
                    cubicTo(
                        x1 = size.width * 0.4f, y1 = size.height * 0.10f,
                        x2 = size.width * 0.6f, y2 = size.height * 0.45f,
                        x3 = size.width, y3 = size.height * 0.35f
                    )
                    lineTo(size.width, 0f)
                    close()
                }
                drawPath(path = path, brush = topWaveBrush)
            }
    ) {
        content()
    }
}

val loginScreenModule = screenModule {
    register<SharedScreen.Login> {
        LoginScreen()
    }
}
