package com.wealthvault.login.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.example.login.ui.LoginScreenModel
import com.wealthvault.core.utils.getScreenModel


class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<LoginScreenModel>()

        // เรียกใช้ UI และส่ง State/Event เข้าไป
        LoginUI(
            username = screenModel.username,
            password = screenModel.password,
            isLoading = screenModel.isLoading,
            onUsernameChange = { screenModel.username = it },
            onPasswordChange = { screenModel.password = it },
            onLoginClick = {
                screenModel.onLoginClick {
                    // navigator.replaceAll(MainScreen())
                    println("success")
                }
            }
        )
    }
}

@Composable
fun LoginUI(
    username: String,
    password: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Wealth &    ",
            style = MaterialTheme.typography.headlineLarge // เพิ่มสไตล์ให้สวยงาม
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onLoginClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Login")
            }
        }
    }
}


@Preview
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        // สามารถจำลองสถานะต่างๆ ได้ทันทีใน Preview
        LoginUI(
            username = "Natchapon",
            password = "password123",
            isLoading = false,
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {}
        )
    }
}

@Preview
@Composable
fun LoginLoadingPreview() {
    MaterialTheme {
        // ลองดูว่าตอนโหลด (Loading) หน้าตาเป็นยังไง
        LoginUI(
            username = "admin",
            password = "123",
            isLoading = true,
            onUsernameChange = {},
            onPasswordChange = {},
            onLoginClick = {}
        )
    }
}
