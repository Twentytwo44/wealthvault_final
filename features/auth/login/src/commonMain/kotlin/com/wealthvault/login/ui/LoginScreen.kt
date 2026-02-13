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
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Wealth & Vault",


            )

            Spacer(modifier = Modifier.height(32.dp))

            TextField(
                value = screenModel.username,
                onValueChange = { screenModel.username = it },

                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = screenModel.password,
                onValueChange = { screenModel.password = it },

                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

//            screenModel.errorMessage?.let {
//                Text(
//                    text = it,
//                    color = Color.Red,
//                    modifier = Modifier.padding(top = 8.dp)
//                )
//            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    screenModel.onLoginClick {
                        // เมื่อ Login สำเร็จ ให้ล้าง Stack และไปหน้า Main
//                        navigator.replaceAll(MainScreen())
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !screenModel.isLoading
            ) {
                if (screenModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Login")
                }
            }
        }
    }
}
