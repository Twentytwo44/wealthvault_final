package com.wealthvault.login.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_auth_google
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSurface
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun LoginActions(
    onLoginClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Spacer(modifier = Modifier.height(10.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Text(
            text = "ลืมรหัสผ่าน",
            color = LightMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clickable(onClick = onForgotPasswordClick)
                .padding(vertical = 2.dp, horizontal = 6.dp),
        )
    }

    Spacer(modifier = Modifier.height(26.dp))
    Button(
        onClick = onLoginClick,
        modifier = Modifier.fillMaxWidth().height(46.dp),
        shape = RoundedCornerShape(percent = 30),
        colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
    ) {
        Text("เข้าสู่ระบบ", style = MaterialTheme.typography.bodyLarge, color = LightSurface)
    }

    Spacer(modifier = Modifier.height(20.dp))
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = "ยังไม่มีบัญชี ", color = LightMuted, style = MaterialTheme.typography.bodyMedium)
        Text(
            text = "สร้างบัญชี?",
            color = LightPrimary,
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onRegisterClick),
        )
    }

    Spacer(modifier = Modifier.height(24.dp))
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp),
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
        Text(
            text = " หรือ ",
            color = LightMuted,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = LightBorder, thickness = 2.dp)
    }

    Spacer(modifier = Modifier.height(24.dp))
    OutlinedButton(
        onClick = onGoogleClick,
        modifier = Modifier.fillMaxWidth().height(46.dp).padding(horizontal = 48.dp),
        shape = RoundedCornerShape(percent = 30),
        border = BorderStroke(1.dp, LightBorder),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = LightSurface),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(Res.drawable.ic_auth_google),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp),
                tint = Color.Unspecified,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Google", color = LightPrimary, style = MaterialTheme.typography.bodyLarge)
        }
    }

    Spacer(modifier = Modifier.height(24.dp))
}
