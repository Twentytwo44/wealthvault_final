package com.wealthvault_final.`financial-asset`.ui.components.maptype

// 🌟 Import Theme ของแอป
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInput(
    label: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "กรุณาเลือก" // 🌟 แก้จาก data: Any? เป็น placeholder เพื่อป้องกันการโชว์คำว่า "null"
) {
    var expanded by remember { mutableStateOf(false) }

    // 💡 ถ้ายังไม่ได้เลือกอะไรเลย ให้ค่าว่างไว้ เพื่อให้มันไปโชว์ Placeholder แทน
    val selectedLabel = options.find { it.first == selectedValue }?.second ?: ""

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // 🌟 ปรับ Label ให้เป็นสี LightPrimary และใช้ bodyMedium ตามสไตล์ Profile
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            // 🌟 เปลี่ยนจาก OutlinedTextField เป็น TextField แบบเดียวกับ AssetTextField
            TextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    // 🌟 วาดกรอบให้โค้ง 12.dp และสีจางๆ แบบ Profile
                    .border(
                        width = 1.dp,
                        color = LightBorder.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    ),
                placeholder = { Text(placeholder, color = Color.LightGray) },
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = TextFieldDefaults.colors(
                    // 🌟 สีพื้นหลัง
                    focusedContainerColor = LightSoftWhite,
                    unfocusedContainerColor = LightSoftWhite,

                    // 🌟 ปิดเส้นขีดด้านล่างเพราะเราวาด border ไปแล้ว
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,

                    // 🌟 สีตัวหนังสือ
                    focusedTextColor = Color(0xFF3A2F2A),
                    unfocusedTextColor = Color(0xFF3A2F2A)
                ),
                singleLine = true
            )

            // ส่วนของเมนู Dropdown
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                containerColor = LightSoftWhite // 🌟 เปลี่ยนพื้นหลังเมนูให้เป็นสีเดียวกับช่อง Input
            ) {
                options.forEach { (type, thaiText) ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = thaiText,
                                color = Color(0xFF3A2F2A) // 🌟 ปรับสีข้อความในเมนูให้คมชัด
                            )
                        },
                        onClick = {
                            onValueChange(type)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}