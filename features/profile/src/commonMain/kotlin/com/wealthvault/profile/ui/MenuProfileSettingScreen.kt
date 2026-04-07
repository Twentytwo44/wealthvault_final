package com.wealthvault.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.ic_common_next // 🌟 ดึงไอคอนลูกศรชี้ขวามาใช้
import com.wealthvault.core.theme.LightBg
import org.jetbrains.compose.resources.painterResource

@Composable
fun MenuProfileSettingScreen(
    onBackClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onShareSettingClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBg)
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 24.dp)

    ) {
        // --- Header ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_back),
                contentDescription = "Back",
                tint = themeColor,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onBackClick() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "ตั้งค่าโปรไฟล์",
                style = MaterialTheme.typography.titleLarge,
                color = themeColor
            )
        }

        // --- Menu Items ---
        SettingMenuItem(title = "แก้ไขโปรไฟล์", onClick = onEditProfileClick)
        Spacer(modifier = Modifier.height(24.dp))

        SettingMenuItem(title = "ตั้งค่าการแชร์ทรัพย์สินให้คนใกล้ชิด", onClick = onShareSettingClick)
        Spacer(modifier = Modifier.height(24.dp))

        // 🌟 สมมติว่าภาษา/ธีม ยังไม่ทำ ก็ใส่คอมเมนต์ไว้ก่อนได้ครับ
        SettingMenuItem(title = "ภาษา", onClick = { /* TODO */ }, showArrow = false)
        Spacer(modifier = Modifier.height(24.dp))

        SettingMenuItem(title = "ธีม", onClick = { /* TODO */ }, showArrow = false)
        Spacer(modifier = Modifier.height(32.dp))

        // --- Logout Button ---
        Text(
            text = "ออกจากระบบ",
            fontSize = 16.sp,
            color = Color(0xFFE74C3C), // สีแดง
            modifier = Modifier.clickable { onLogoutClick() }
        )
    }
}

// 🌟 Component ย่อยสำหรับเมนูแต่ละบรรทัด
@Composable
fun SettingMenuItem(title: String, onClick: () -> Unit, showArrow: Boolean = true) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp), // ขยายพื้นที่กดนิดนึง
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFF3A2F2A)
        )
        if (showArrow) {
            Icon(
                painter = painterResource(Res.drawable.ic_common_next),
                contentDescription = "Go",
                tint = Color(0xFFC27A5A),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}