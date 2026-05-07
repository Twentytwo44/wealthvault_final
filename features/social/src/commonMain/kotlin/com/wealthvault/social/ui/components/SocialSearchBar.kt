package com.wealthvault.social.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_search
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightSoftWhite
import org.jetbrains.compose.resources.painterResource

@Composable
fun SocialSearchBar(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    placeholderText: String = "ค้นหาด้วยชื่อ",
    themeColor: Color = Color(0xFFC27A5A)
) {
    BasicTextField(
        value = searchQuery,
        onValueChange = onSearchChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 14.sp),
        cursorBrush = SolidColor(themeColor), // 🌟 ใช้ themeColor เป็นสีเคอร์เซอร์เพื่อให้เข้ากับธีม
        modifier = modifier.height(52.dp),    // 🌟 ล็อกความสูงเป๊ะๆ ที่ 52.dp
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightSoftWhite, RoundedCornerShape(12.dp))
                    .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 16.dp), // ระยะห่างด้านซ้าย-ขวา
                verticalAlignment = Alignment.CenterVertically // 🌟 จัดข้อความกึ่งกลางแนวตั้ง (ไม่จม)
            ) {
                // ไอคอนแว่นขยาย
                Icon(
                    painter = painterResource(Res.drawable.ic_common_search),
                    contentDescription = "Search",
                    tint = LightBorder,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                // ช่องพิมพ์ข้อความ & Placeholder
                Box(modifier = Modifier.weight(1f)) {
                    if (searchQuery.isEmpty()) {
                        Text(text = placeholderText, color = Color.Gray, fontSize = 14.sp)
                    }
                    innerTextField()
                }
            }
        }
    )
}