package com.wealthvault.login.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_auth_email
import com.wealthvault.core.generated.resources.ic_auth_eye
import com.wealthvault.core.generated.resources.ic_auth_eye_slash
import com.wealthvault.core.generated.resources.ic_auth_lock
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.RedErr
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun LoginFields(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    errorMessage: String?,
) {
    val hasError = errorMessage != null

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 8.dp, end = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
            ) {
                Text(
                    text = "อีเมล",
                    color = LightPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                if (hasError) {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = RedErr,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }

            BasicTextField(
                value = username,
                onValueChange = onUsernameChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                cursorBrush = SolidColor(LightPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LightSurface, RoundedCornerShape(percent = 30))
                            .border(1.dp, LightBorder, RoundedCornerShape(percent = 30))
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_auth_email),
                            contentDescription = "email",
                            tint = LightPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (username.isEmpty()) Text("อีเมล", color = Color.Gray)
                            innerTextField()
                        }
                    }
                },
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        var isPasswordVisible by remember { mutableStateOf(false) }

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "รหัสผ่าน",
                color = LightPrimary,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp, start = 8.dp),
            )
            BasicTextField(
                value = password,
                onValueChange = onPasswordChange,
                singleLine = true,
                visualTransformation = if (isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                textStyle = LocalTextStyle.current.copy(color = Color.Black),
                cursorBrush = SolidColor(LightPrimary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(LightSurface, RoundedCornerShape(percent = 30))
                            .border(1.dp, LightBorder, RoundedCornerShape(percent = 30))
                            .padding(start = 16.dp, end = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_auth_lock),
                            contentDescription = "lock",
                            tint = LightPrimary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (password.isEmpty()) Text("รหัสผ่าน", color = Color.Gray)
                            innerTextField()
                        }
                        IconButton(
                            onClick = { isPasswordVisible = !isPasswordVisible },
                            modifier = Modifier.size(44.dp),
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (isPasswordVisible) {
                                        Res.drawable.ic_auth_eye
                                    } else {
                                        Res.drawable.ic_auth_eye_slash
                                    },
                                ),
                                contentDescription = "Toggle Password Visibility",
                                tint = LightPrimary,
                                modifier = Modifier.size(22.dp),
                            )
                        }
                    }
                },
            )
        }
    }
}
