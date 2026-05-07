package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_clock
import com.wealthvault.core.generated.resources.ic_common_solid_right
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.RedErr
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource

@Composable
fun ActivityBubbleCard(
    title: String,
    assetName: String,
    assetType: String,
    isMe: Boolean,
    profileImageUrl: String? = null,
    isDeleted: Boolean = false,
    shareAtDisplay: String? = null, // 🌟 เพิ่ม Parameter รับวันที่ "2026-05-28T00:00:00Z"
    themeColor: Color = Color(0xFFC27A5A),
    onDetailClick: () -> Unit = {}
) {
    val thaiAssetType = when {
        assetType.contains("account", ignoreCase = true) -> "บัญชีเงินฝาก"
        assetType.contains("cash", ignoreCase = true) -> "เงินสด ทองคำ"
        assetType.contains("investment", ignoreCase = true) -> "ลงทุน หุ้น กองทุน"
        assetType.contains("insurance", ignoreCase = true) -> "ประกัน"
        assetType.contains("building", ignoreCase = true) -> "บ้าน ตึก อาคาร"
        assetType.contains("land", ignoreCase = true) -> "ที่ดิน"
        assetType.contains("loan", ignoreCase = true) || assetType.contains("liability", ignoreCase = true) -> "หนี้สิน"
        assetType.contains("expense", ignoreCase = true) -> "ค่าใช้จ่ายระยะยาว"
        else -> assetType
    }

    // 🌟 เช็กว่าวันที่แชร์อยู่ล่วงหน้า (อนาคต) หรือไม่
    val isFutureShare = remember(shareAtDisplay, isDeleted) {
        if (isDeleted || shareAtDisplay.isNullOrBlank()) {
            false
        } else {
            try {
                val datePart = shareAtDisplay.substringBefore("T")
                val todayStr = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
                datePart > todayStr
            } catch (e: Exception) {
                false
            }
        }
    }

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
            modifier = (if (isMe) Modifier.fillMaxWidth(0.80f) else Modifier.fillMaxWidth(0.85f))
                .then(
                    // 🌟 ถ้าเป็นแชร์ล่วงหน้า ให้วาดเส้นขอบแบบ "เส้นประ" (Dashed Border)
                    if (isFutureShare) {
                        Modifier.drawWithContent {
                            drawContent()
                            drawRoundRect(
                                color = themeColor.copy(alpha = 0.5f),
                                style = Stroke(
                                    width = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                ),
                                cornerRadius = androidx.compose.ui.geometry.CornerRadius(16.dp.toPx())
                            )
                        }
                    } else Modifier
                ),
            colors = CardDefaults.cardColors(
                containerColor = if (isDeleted) LightBg else LightSoftWhite
            ),
            shape = RoundedCornerShape(
                topStart = if (isMe) 16.dp else 4.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = if (isMe) 4.dp else 16.dp
            ),
            // ปิดขอบปกติถ้าใช้เส้นประไปแล้ว
            border = if (isFutureShare) null else BorderStroke(1.dp, themeColor.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // 🌟 หัวข้อ
                Text(
                    text = title,
                    fontSize = 14.sp,
                    color = if (isDeleted) Color.Gray else Color(0xFF3A2F2A),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ชื่อทรัพย์สิน", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = assetName,
                        fontSize = 12.sp,
                        color = if (isDeleted) Color.Gray else Color(0xFF3A2F2A),
                        textDecoration = if (isDeleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null,
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "ประเภท", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = thaiAssetType,
                        fontSize = 12.sp,
                        color = if (isDeleted) Color.Gray else Color(0xFF3A2F2A),
                        textAlign = TextAlign.End,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 🌟 จัดการปุ่มด้านล่าง
                if (isDeleted) {
                    Text(
                        text = "ทรัพย์สินนี้ถูกลบหรือยกเลิกการแชร์แล้ว",
                        fontSize = 12.sp,
                        color = RedErr,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.align(Alignment.End).padding(vertical = 4.dp)
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isFutureShare) Arrangement.SpaceBetween else Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 🌟 โชว์สถานะอัปเดตล่วงหน้า (อยู่ฝั่งซ้าย)
                        if (isFutureShare && !shareAtDisplay.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_common_clock),
                                    contentDescription = null,
                                    tint = LightMuted,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    // ตรงนี้ถ้าอยากใช้ formatThaiDate(shareAtDisplay) ของคุณแชมป์ ก็เปลี่ยนได้เลยนะครับ
                                    text = " ${shareAtDisplay.substringBefore("T")}",
                                    fontSize = 12.sp,
                                    color = LightMuted
                                )
                            }
                        }

                        // ปุ่มรายละเอียด (อยู่ฝั่งขวา)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onDetailClick() }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "รายละเอียด",
                                fontSize = 12.sp,
                                color = themeColor,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                painter = painterResource(Res.drawable.ic_common_solid_right),
                                contentDescription = null,
                                tint = themeColor,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}