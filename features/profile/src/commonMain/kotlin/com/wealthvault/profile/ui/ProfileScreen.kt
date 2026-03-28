package com.wealthvault.profile.ui

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator // 🌟 เพิ่ม Loading
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState // 🌟 เพิ่ม import นี้
import androidx.compose.runtime.getValue // 🌟 เพิ่ม import นี้
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_profile_setting
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.profile.ui.components.ClosePersonItem
import com.wealthvault.`user-api`.model.UserData // 🌟 นำเข้าโมเดล UserData
import org.jetbrains.compose.resources.painterResource

class ProfileScreen(private val onSettingsClick: () -> Unit) : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<ProfileScreenModel>()

        // 🌟 1. ดักฟังข้อมูลจาก ScreenModel (ถ้ายังโหลดไม่เสร็จจะเป็น null)
        val userData by screenModel.userState.collectAsState()

        ProfileContent(
            userData = userData, // 🌟 2. โยนข้อมูลไปให้ UI
            onSettingsClick = onSettingsClick
        )
    }
}

@Composable
fun ProfileContent(
    userData: UserData?, // 🌟 รับข้อมูลมาใช้งาน (อาจจะ null ได้)
    onSettingsClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val bgColor = Color(0xFFFFF8F3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp)
    ) {

        // --- Header ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "โปรไฟล์",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                color = themeColor
            )

            Icon(
                painter = painterResource(Res.drawable.ic_profile_setting),
                contentDescription = "Settings",
                tint = Color(0xFFC47B5D),
                modifier = Modifier
                    .size(28.dp)
                    .clickable { onSettingsClick() }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Profile Info ---
        // 🌟 3. เช็คว่าโหลดข้อมูลเสร็จหรือยัง ถ้ายังให้โชว์หมุนๆ
        if (userData == null) {
            Box(modifier = Modifier.fillMaxWidth().height(90.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
        } else {
            // 🌟 4. โหลดเสร็จแล้ว! เอาข้อมูลมาแปะเลย
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0DCDA))
                )
                Spacer(modifier = Modifier.width(20.dp))
                Column {
                    Text(
                        text = userData.username, // 👈 ใช้ชื่อจาก API
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF3A2F2A)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = userData.email, // 👈 ใช้อีเมลจาก API
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        // ตัดเอาแค่วันที่ (ตัวอย่าง: 1970-01-01) หรือจะใช้ตัวจัดรูปแบบวันที่เพิ่มเติมก็ได้ครับ
                        text = userData.birthday.take(10),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF3A2F2A)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(24.dp))

        // --- Settings Summary ---
        // ซ่อนส่วนนี้ไว้ก่อนถ้ายกเลิกการแชร์ หรือจะโชว์ตลอดก็ได้ครับ
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "เปิดให้เห็นสินทรัพย์ทั้งหมดให้คนใกล้ชิดเมื่ออายุ",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFF3A2F2A),
                modifier = Modifier.weight(1f)
            )
            Text(
                // 🌟 โชว์อายุที่แชร์จาก API หรือถ้าไม่แชร์ก็บอกว่า "ไม่ได้เปิดใช้งาน"
                text = if (userData?.shareEnabled == true) "${userData.sharedAge} ปี" else "ไม่ได้เปิดใช้งาน",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Close People List ---
        Text(
            text = "คนใกล้ชิด",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF3A2F2A)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // สมมติว่ามี Component ClosePersonItem อยู่แล้ว (รอ API คนสนิทในอนาคต)
        ClosePersonItem(name = "Nai")
    }
}