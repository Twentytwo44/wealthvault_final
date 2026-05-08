package com.wealthvault_final.`financial-asset`.ui.components.maptype

// 🌟 Import Theme และ Resource
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_solid_down
import com.wealthvault.core.generated.resources.ic_common_solid_up
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import org.jetbrains.compose.resources.painterResource

@Composable
fun DropdownInput(
    label: String,
    options: List<Pair<String, String>>,
    selectedValue: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "กรุณาเลือก"
) {
    var expanded by remember { mutableStateOf(false) }

    // 💡 หา Text ภาษาไทยที่จะแสดงผล อิงจาก Value (Type) ที่เลือก
    val displayLabel = options.find { it.first == selectedValue }?.second

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // 🌟 Label ด้านบน
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = LightPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 🔹 Custom Dropdown ตาม Design ใหม่
        Column(
            modifier = Modifier
                .fillMaxWidth()
                // 🌟 ใช้ความโค้ง 12.dp เพื่อให้ขนาดเท่ากับ TextField ตัวอื่นๆ ในแอปพอดี
                .border(1.dp, LightBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .background(LightSoftWhite, RoundedCornerShape(12.dp))
                .animateContentSize()
        ) {
            // ส่วนที่แสดงผลตอนพับอยู่
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    // โชว์ Placeholder ถ้ายังไม่ได้เลือก
                    text = displayLabel ?: placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (displayLabel != null) LightText else Color.LightGray
                )
                Icon(
                    painter = if (expanded) painterResource(Res.drawable.ic_common_solid_up) else painterResource(Res.drawable.ic_common_solid_down),
                    contentDescription = null,
                    tint = LightPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // รายการให้เลือกเมื่อกางเมนูออก
            if (expanded) {
                options.forEach { (type, thaiText) ->
                    // 💡 เช็คเพื่อไม่ให้โชว์ตัวที่ถูกเลือกไปแล้วซ้ำอีกรอบ
                    if (type != selectedValue) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = LightBg,
                            thickness = 1.dp
                        )
                        Text(
                            text = thaiText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = LightText,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onValueChange(type) // ส่งค่ากลับไป
                                    expanded = false    // พับเมนูเก็บ
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}