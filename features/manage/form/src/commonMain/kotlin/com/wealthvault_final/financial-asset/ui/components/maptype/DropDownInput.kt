package com.wealthvault_final.`financial-asset`.ui.components.maptype

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownInput(
    label: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    data: Any? = null
) {
    var expanded by remember { mutableStateOf(false) }

    // 💡 ถ้ายังไม่ได้เลือกอะไรเลย ให้ค่าว่างไว้ เพื่อให้มันไปโชว์ Placeholder แทน
    val selectedLabel = options.find { it.first == selectedValue }?.second ?: ""

    // 🌟 1. ใช้โครงสร้าง Column แบบเดียวกับ AssetTextField
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // 🌟 2. ดึงหัวข้อ (Label) มาไว้ด้านบน ใช้สีและสไตล์เดียวกันเป๊ะ
        Text(text = label, color = Color(0xFFBCAAA4), style = MaterialTheme.typography.bodySmall)

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedLabel,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                // 🌟 3. ใส่ Placeholder สีเทาแบบ AssetTextField
                placeholder = { Text(data.toString(), color = Color.LightGray) },
                // 🌟 4. ปรับขอบโค้ง 12.dp
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                // 🌟 5. ก๊อปปี้ชุดสีมาจาก AssetTextField แบบ 100%
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFFB08968),
                    unfocusedBorderColor = Color(0xFFE0E0E0)
                )
            )

            // ส่วนของเมนู Dropdown (คงไว้เหมือนเดิม)
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                // (Option เสริม) ถ้าอยากให้พื้นหลังเมนูเป็นสีขาวด้วย
                containerColor = Color.White
            ) {
                options.forEach { (type, thaiText) ->
                    DropdownMenuItem(
                        text = { Text(thaiText) },
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
