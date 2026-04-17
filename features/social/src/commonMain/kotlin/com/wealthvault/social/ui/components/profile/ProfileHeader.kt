package com.wealthvault.social.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.WvWaveGradientEnd
import org.jetbrains.compose.resources.painterResource

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String,
    profileImageUrl: String? = null,
    isFriend: Boolean = false,
    username: String = "-",
    email: String = "-",
    fullName: String = "-",
    phoneNumber: String = "-",
    birthDate: String = "-"
) {
    val themeColor = Color(0xFFC27A5A)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- รูปโปรไฟล์ ---
        Box(
            modifier = Modifier
                .size(120.dp)
                .border(width = 3.dp, color = LightPrimary, shape = CircleShape)
                .padding(3.dp)
                .clip(CircleShape)
                .background(LightBg),
            contentAlignment = Alignment.Center
        ) {
            val hasValidImage = !profileImageUrl.isNullOrEmpty()

            if (hasValidImage) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_profile),
                    contentDescription = "Default Profile",
                    tint = WvWaveGradientEnd,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        // 🌟 เช็คเงื่อนไข: ถ้า "ไม่ได้เป็นเพื่อน" ถึงจะโชว์ Username และ Email ใต้รูป
        if (!isFriend) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- ชื่อ และ ซับไตเติ้ล ---
            Text(
                text = name,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF3A2F2A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // 🌟 แสดงกล่องข้อมูลส่วนตัวเฉพาะเมื่อเป็นเพื่อนกัน (แทนที่ชื่อใต้รูปไปเลย)
        if (isFriend) {
            Spacer(modifier = Modifier.height(24.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = LightSoftWhite),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, LightBorder),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ข้อมูลส่วนตัว",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileInfoRow(label = "ชื่อผู้ใช้", value = username)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.6f), thickness = 1.dp)
                    ProfileInfoRow(label = "อีเมล", value = email)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.6f), thickness = 1.dp)
                    ProfileInfoRow(label = "ชื่อ-นามสกุล", value = fullName)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.6f), thickness = 1.dp)
                    ProfileInfoRow(label = "เบอร์โทรศัพท์", value = phoneNumber)
                    HorizontalDivider(color = Color.LightGray.copy(alpha = 0.6f), thickness = 1.dp)
                    ProfileInfoRow(label = "วันเกิด", value = birthDate)
                }
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFF9E918B),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color(0xFF3A2F2A),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}