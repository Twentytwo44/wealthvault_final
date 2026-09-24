package com.wealthvault.financiallist.ui.shareasset

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import org.jetbrains.compose.resources.painterResource

@Composable
fun SectionHeader(
    title: String,
    showInfo: Boolean = false,
    onInfoClick: () -> Unit = {},
    onAddClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(title, color = LightPrimary, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            if (showInfo) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    Icons.Default.Info,
                    null,
                    modifier = Modifier.size(20.dp).clickable(onClick = onInfoClick),
                    tint = LightPrimary.copy(alpha = 0.6f),
                )
            }
        }
        IconButton(onClick = onAddClick) {
            Icon(
                painterResource(Res.drawable.ic_common_plus),
                null,
                tint = LightPrimary,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
internal fun ShareLoadingState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.CircularProgressIndicator(color = LightPrimary)
    }
}

@Composable
internal fun ShareLoadError(error: AppError, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = shareErrorMessage(error),
                color = LightText,
                style = MaterialTheme.typography.bodyMedium,
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
            ) {
                Text("ลองใหม่", color = Color.White)
            }
        }
    }
}

@Composable
internal fun ShareInlineError(error: AppError, onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF0EE), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = shareErrorMessage(error),
            color = LightText,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
        )
        androidx.compose.material3.TextButton(onClick = onRetry) {
            Text("ลองใหม่", color = LightPrimary)
        }
    }
}

private fun shareErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบข้อมูลการแชร์ของทรัพย์สินนี้"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดข้อมูลการแชร์ไม่สำเร็จ กรุณาลองใหม่"
}
