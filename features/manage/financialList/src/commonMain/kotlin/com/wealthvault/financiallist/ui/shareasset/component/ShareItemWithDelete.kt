package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_bin
import com.wealthvault.core.generated.resources.ic_form_email_outline
import com.wealthvault.core.generated.resources.ic_nav_profile
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightMuted
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.theme.RedErr
import com.wealthvault.core.utils.formatThaiDate // 🌟 นำเข้าฟังก์ชันแปลงวันที่ของคุณแชมป์
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShareItemWithDelete(data: ShareInfo, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 🌟 ประมวลผลวันที่: คืนค่าเป็น String ถ้าวันแชร์ > วันนี้, ถ้าแชร์แล้วคืนค่า null (เพื่อซ่อน)
    val displayFutureDateText = remember(data.date) {
        val dateStr = data.date
        if (dateStr.isNullOrBlank()) {
            null
        } else {
            try {
                // ตัดเอาแค่ 2026-05-30
                val datePart = dateStr.substringBefore("T")

                // 🌟 คราวนี้โค้ดจะคลีนๆ ดึงวันที่ปัจจุบันมาเทียบได้ตรงๆ โดยไม่มีขีดแดงแล้วครับ
                val todayStr = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

                // ถ้าวันที่ใน Database มากกว่าวันนี้ ค่อยแปลง Format ไปแสดงผล
                if (datePart > todayStr) {
                    formatThaiDate(datePart)
                } else {
                    null // คืนค่า null เพื่อซ่อนวันที่ (แชร์ไปแล้ว)
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().background(LightBg),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LightSoftWhite),
        border = BorderStroke(1.dp, LightBorder),
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image / Placeholder
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(LightBg),
                contentAlignment = Alignment.Center
            ) {
                if (!data.profileUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = data.profileUrl,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    val icon = when (data.typeData) {
                        "E" -> painterResource(Res.drawable.ic_form_email_outline)
                        "G" -> painterResource(Res.drawable.ic_nav_social)
                        else -> painterResource(Res.drawable.ic_nav_profile)
                    }
                    Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.name ?: "",
                    style = MaterialTheme.typography.bodyLarge,
                    color = LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (data.typeData == "E") {
                    if (displayFutureDateText != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        // 🌟 แสดงวันที่กรณีแชร์แบบ Email (ถ้ายังไม่ถึงเวลา)
                        Text(
                            text = displayFutureDateText,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.Gray // 🌟 ใช้สีเทาตามภาพตัวอย่าง
                        )
                    }
                } else {
                    if (data.subText.isNotBlank() || displayFutureDateText != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            if (data.subText.isNotBlank()) {
                                Row(
                                    modifier = Modifier
                                        .weight(1f, fill = false)
                                        //  .background(LightBg, RoundedCornerShape(200.dp)) // (เอาพื้นหลังเทาออกถ้าจะให้เหมือนในรูป หรือใส่ไว้ตามชอบครับ)
                                        .padding(horizontal = 2.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = if(data.typeData == "G") painterResource(Res.drawable.ic_nav_social) else painterResource(Res.drawable.ic_nav_profile)
                                    Icon(painter = icon, contentDescription = null, tint = LightPrimary, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = data.subText,
                                        color = LightPrimary,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            if (displayFutureDateText != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                // 🌟 แสดงวันที่มุมขวาล่างสำหรับเพื่อน/กลุ่ม (ถ้ายังไม่ถึงเวลา)
                                Text(
                                    text = displayFutureDateText,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray, // 🌟 สีเทาตามรูป
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // เปิด Popup ถังขยะ
            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_bin),
                    contentDescription = "Delete",
                    tint = RedErr,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Popup ยืนยันการลบ
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = "ยกเลิกการแชร์",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )
            },
            text = {
                Text(
                    text = "คุณแน่ใจหรือไม่ว่าต้องการยกเลิกการแชร์ข้อมูลนี้ให้ '${data.name ?: "บุคคลนี้"}' ?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightMuted,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDelete()
                    }
                ) {
                    Text("ยกเลิกการแชร์", color = RedErr, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("ปิด", color = LightMuted, fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}