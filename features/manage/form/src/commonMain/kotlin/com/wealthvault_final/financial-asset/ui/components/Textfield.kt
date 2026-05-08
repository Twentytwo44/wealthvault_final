package com.wealthvault_final.`financial-asset`.ui.components

// 🌟 Import Theme ของแอป
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite

@Composable
fun AssetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    isMultiLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default // 🌟 รับ KeyboardOptions เหมือนเดิม
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // 🌟 ปรับ Label ให้เป็นสี LightPrimary และใช้ bodyMedium ตามสไตล์ Profile
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🌟 เปลี่ยนมาใช้ TextField ธรรมดาแล้ววาดกรอบเอง
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.LightGray) },
            modifier = Modifier
                .fillMaxWidth()
                // 🌟 จัดการความสูงถ้าเป็น MultiLine
                .then(if (isMultiLine) Modifier.height(120.dp) else Modifier)
                // 🌟 วาดกรอบให้โค้ง 12.dp และสีจางๆ แบบ Profile
                .border(
                    width = 1.dp,
                    color = LightBorder.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(12.dp)
                ),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                // 🌟 สีพื้นหลัง
                focusedContainerColor = LightSoftWhite,
                unfocusedContainerColor = LightSoftWhite,

                // 🌟 ปิดเส้นขีดด้านล่างเพราะเราวาด border ไปแล้ว
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,

                // 🌟 สีตัวหนังสือตอนพิมพ์
                focusedTextColor = Color(0xFF3A2F2A),
                unfocusedTextColor = Color(0xFF3A2F2A)
            ),
            keyboardOptions = keyboardOptions,
            singleLine = !isMultiLine // 🌟 ถ้าไม่ได้ตั้งเป็น multiLine ให้เป็น singleLine
        )
    }
}