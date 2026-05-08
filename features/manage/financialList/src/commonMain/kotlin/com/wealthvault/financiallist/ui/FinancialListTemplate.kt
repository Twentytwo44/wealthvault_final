package com.wealthvault.financiallist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
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
fun FinancialListTemplate(
    headerTitle: String,
    themeColor: Color, // สีหลักของหน้า (ส้ม สำหรับทรัพย์สิน, แดง สำหรับหนี้สิน)
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddClick: () -> Unit,
    headerIcon: @Composable () -> Unit, // ช่องสำหรับใส่ Icon หัวข้อ
    content: @Composable () -> Unit // ช่องว่างตรงกลางสำหรับใส่รายการข้อมูล (LazyColumn)
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {
        // --- 1. ส่วนหัวข้อ (Header) ---
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // วาด Icon ที่ส่งเข้ามา
            headerIcon()

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = headerTitle,
                style = MaterialTheme.typography.titleLarge,
                color = themeColor
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 2. ช่องค้นหา (Search Bar) ---
        // 🌟 เปลี่ยนมาใช้ BasicTextField เพื่อจัดกึ่งกลางและไม่จมขอบ
        BasicTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = Color.Black, fontSize = 14.sp),
            cursorBrush = SolidColor(themeColor), // 🌟 สีเคอร์เซอร์เข้ากับธีม (ส้ม/แดง)
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp),
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightSoftWhite, RoundedCornerShape(12.dp))
                        .border(1.dp, LightBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically // 🌟 จัดข้อความกึ่งกลางแนวตั้งเป๊ะๆ
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_search),
                        contentDescription = "Search",
                        tint = LightBorder,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(modifier = Modifier.weight(1f)) {
                        if (searchQuery.isEmpty()) {
                            Text(text = "ค้นหาด้วยชื่อ", color = Color.Gray, fontSize = 14.sp)
                        }
                        innerTextField()
                    }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 3. พื้นที่สำหรับใส่ข้อมูลลิสต์ ---
        // โค้ดส่วนนี้จะดึง UI จากหน้า Asset หรือ Debt มาแสดง
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}