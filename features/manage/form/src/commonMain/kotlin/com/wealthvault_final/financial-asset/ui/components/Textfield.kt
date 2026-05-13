package com.wealthvault_final.`financial-asset`.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // --- 1. ส่วน Label ---
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))

        // --- 2. ส่วนช่องกรอก (BasicTextField) ---
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = !isMultiLine,
            keyboardOptions = keyboardOptions,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFF3A2F2A)),
            cursorBrush = SolidColor(LightPrimary),
            modifier = Modifier
                .fillMaxWidth()
                // 🌟 ถ้าเป็น MultiLine ให้สูง 120.dp ถ้าปกติสูง 44.dp
                .height(if (isMultiLine) 120.dp else 44.dp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightSoftWhite, RoundedCornerShape(12.dp))
                        .border(
                            width = 1.dp,
                            color = LightBorder.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = if (isMultiLine) 12.dp else 0.dp // เพิ่ม padding บน-ล่างถ้าเป็นหลายบรรทัด
                        ),
                    // 🌟 ถ้าเป็น MultiLine ให้เริ่มพิมพ์จากด้านบน ถ้าบรรทัดเดียวให้อยู่ตรงกลาง
                    verticalAlignment = if (isMultiLine) Alignment.Top else Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = Color.LightGray,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        innerTextField()
                    }
                }
            }
        )
    }
}