package com.wealthvault.core.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.Font

// 🌟 Import ไฟล์ Res ของฟอนต์ Kanit 🌟
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.kanit_regular
import com.wealthvault.core.generated.resources.kanit_medium
import com.wealthvault.core.generated.resources.kanit_bold
import com.wealthvault.core.generated.resources.notosansthai_regular
import com.wealthvault.core.generated.resources.notosansthai_medium
import com.wealthvault.core.generated.resources.notosansthai_bold

@Composable
fun getAppTypography(): Typography {

    // 1. ดึงฟอนต์ Kanit เข้ามาใช้งาน
//    val appFontFamily = FontFamily(
//        Font(resource = Res.font.kanit_regular, weight = FontWeight.Normal),
//        Font(resource = Res.font.kanit_medium, weight = FontWeight.Medium),
//        Font(resource = Res.font.kanit_bold, weight = FontWeight.Bold)
//    )
    val appFontFamily = FontFamily(
        Font(resource = Res.font.notosansthai_regular, weight = FontWeight.Normal),
        Font(resource = Res.font.notosansthai_medium, weight = FontWeight.Medium),
        Font(resource = Res.font.notosansthai_bold, weight = FontWeight.Bold)
    )

    // 2. จับคู่ขนาดฟอนต์ตาม Figma
    return Typography(
        // โชว์ยอดเงินใหญ่ๆ (32)
        headlineLarge = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 32.sp
        ),

        // หัวข้อหน้าจอ (24)
        titleLarge = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp
        ),

        // หัวข้อการ์ด / หัวเรื่องรอง (20)
        titleMedium = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 20.sp
        ),

        // เนื้อหาหลัก / ชื่อรายการ (16) -> ลดจาก 18
        bodyLarge = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        ),

        // เนื้อหารอง / คำอธิบาย (14) -> ลดจาก 15
        bodyMedium = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp
        ),

        // ป้ายกำกับเล็กๆ / วันที่ / ข้อความใน Navbar (12)
        labelMedium = TextStyle(
            fontFamily = appFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp
        )
    )
}