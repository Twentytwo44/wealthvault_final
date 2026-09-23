package com.wealthvault.register.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.core.theme.*
import com.wealthvault.core.generated.resources.Res
import org.jetbrains.compose.resources.painterResource
import com.wealthvault.core.generated.resources.register
import com.wealthvault.core.generated.resources.ic_auth_email
import com.wealthvault.core.generated.resources.ic_auth_google
import com.wealthvault.core.generated.resources.ic_auth_eye
import com.wealthvault.core.generated.resources.ic_auth_eye_slash
import com.wealthvault.core.generated.resources.ic_auth_lock

class RegisterScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<RegisterScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()

        RegisterContent(
            username = uiState.username,
            onUsernameChange = { screenModel.onAction(RegisterUiAction.UsernameChanged(it)) },
            password = uiState.password,
            onPasswordChange = { screenModel.onAction(RegisterUiAction.PasswordChanged(it)) },
            confirmPassword = uiState.confirmPassword,
            onConfirmPasswordChange = { screenModel.onAction(RegisterUiAction.ConfirmPasswordChanged(it)) },
            isLoading = uiState.isLoading,
            errorMessage = uiState.errorMessage,
            onRegisterClick = {
                val validationError = validateRegistrationInput(
                    uiState.username,
                    uiState.password,
                    uiState.confirmPassword,
                )
                if (!uiState.isLoading && validationError == null) {
                    screenModel.onRegisterClick {
                        navigator.pop()
                    }
                } else {
                    validationError?.let { screenModel.errorMessage = it }
                }
            },
            onLoginClick = { navigator.pop() },
            onGoogleClick = { screenModel.onGoogleClick { navigator.pop() } }
        )
    }
}

@Composable
fun RegisterContent(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    isLoading: Boolean,
    errorMessage: String?,
    onRegisterClick: () -> Unit,
    onGoogleClick: () -> Unit
) {
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .imePadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Wealth & Vault",
                color = LightPrimary,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(Res.drawable.register),
                contentDescription = "Register Illustration",
                modifier = Modifier
                    .size(150.dp)
                    .padding(10.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(16.dp))
            RegisterForm(
                username = username,
                onUsernameChange = onUsernameChange,
                password = password,
                onPasswordChange = onPasswordChange,
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = onConfirmPasswordChange,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onRegisterClick = onRegisterClick,
                onLoginClick = onLoginClick,
            )
        }
    }
}

// 🌟 นำ isError ออกจากพารามิเตอร์ เพื่อไม่ให้มันรับค่ามาวาดเส้นสีแดงแล้วครับ
@Composable
fun CustomRegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: org.jetbrains.compose.resources.DrawableResource,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordVisibleChange: (Boolean) -> Unit = {}
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 16.sp),
        cursorBrush = SolidColor(LightPrimary), // กลับมาใช้สีส้มปกติ
        modifier = Modifier.fillMaxWidth().height(44.dp),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightSurface, RoundedCornerShape(percent = 30))
                    .border(
                        width = 1.dp,
                        color = LightBorder, // สีขอบปกติเสมอ
                        shape = RoundedCornerShape(percent = 30)
                    )
                    .padding(start = 16.dp, end = if (isPassword) 4.dp else 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(leadingIcon),
                    contentDescription = null,
                    tint = LightPrimary, // สีไอคอนปกติเสมอ
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(placeholder, color = Color.Gray)
                    }
                    innerTextField()
                }
                if (isPassword) {
                    val icon = if (isPasswordVisible) painterResource(Res.drawable.ic_auth_eye) else painterResource(Res.drawable.ic_auth_eye_slash)
                    IconButton(
                        onClick = { onPasswordVisibleChange(!isPasswordVisible) },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(painter = icon, contentDescription = "Toggle", tint = LightPrimary, modifier = Modifier.size(22.dp)) // สีลูกตาปกติเสมอ
                    }
                }
            }
        }
    )
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
