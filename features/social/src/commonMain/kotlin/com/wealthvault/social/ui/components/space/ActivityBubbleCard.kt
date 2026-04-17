package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_solid_right
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import org.jetbrains.compose.resources.painterResource

@Composable
fun ActivityBubbleCard(
    title: String,
    assetName: String,
    assetType: String,
    isMe: Boolean,
    profileImageUrl: String? = null,
    themeColor: Color = Color(0xFFC27A5A),
    onDetailClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {

        // --- ฝั่งเพื่อน (ด้านซ้าย) ---
        if (!isMe) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(LightBg),
                contentAlignment = Alignment.Center
            ) {
                if (!profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        painter = painterResource(Res.drawable.ic_nav_profile),
                        contentDescription = null,
                        tint = LightPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // --- ตัวการ์ด ---
        Card(
            modifier = if (isMe) Modifier.fillMaxWidth(0.80f) else Modifier.fillMaxWidth(0.85f),
            colors = CardDefaults.cardColors(
                containerColor = if (isMe) themeColor.copy(alpha = 0.05f) else Color.White
            ),
            shape = RoundedCornerShape(
                topStart = if (isMe) 16.dp else 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            border = BorderStroke(1.dp, themeColor.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = title, fontSize = 14.sp, color = Color(0xFF3A2F2A), fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ชื่อทรัพย์สิน", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = assetName,
                        fontSize = 12.sp,
                        color = Color(0xFF3A2F2A),
                        textAlign = TextAlign.End, // 🌟 2. ดันตัวหนังสือให้ชิดขวา
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ประเภท", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = assetType,
                        fontSize = 12.sp,
                        color = Color(0xFF3A2F2A),
                        textAlign = TextAlign.End, // 🌟 2. ดันตัวหนังสือให้ชิดขวา
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.End) // 🌟 ดันทั้ง Row ไปชิดขวาของการ์ด (ถ้าอยู่ใน Column)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onDetailClick() } // 🌟 ให้กดได้ทั้งกลุ่ม (Text + Icon)
                        .padding(vertical = 4.dp), // เพิ่มพื้นที่กดนิดนึง
                    verticalAlignment = Alignment.CenterVertically // 🌟 ให้ Text กับ Icon กึ่งกลางแนวตั้งเท่ากัน
                ) {
                    Text(
                            text = "รายละเอียด", // ปรับชื่อตามความชอบเลยครับ
                    fontSize = 12.sp,
                    color = themeColor,
                    fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.width(4.dp)) // ระยะห่างเล็กน้อยระหว่างตัวหนังสือกับหัวลูกศร

                    Icon(
                        painter = painterResource(Res.drawable.ic_common_solid_right),
                        contentDescription = null,
                        tint = themeColor, // 🌟 ใช้สีเดียวกับ Text จะดูเป็นกลุ่มเดียวกันสวยกว่าครับ
                        modifier = Modifier.size(14.dp) // ปรับขนาดไอคอนให้พอดีกับฟอนต์ 12.sp
                    )
                }

            }
        }
    }
}