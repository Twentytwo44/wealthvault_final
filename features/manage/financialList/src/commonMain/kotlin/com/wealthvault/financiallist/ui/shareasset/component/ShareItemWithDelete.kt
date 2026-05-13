package com.wealthvault.financiallist.ui.shareasset.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.wealthvault.core.theme.*
import com.wealthvault.core.utils.formatThaiDate
import com.wealthvault.financiallist.ui.shareasset.model.ShareInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource

@Composable
fun ShareItemWithDelete(data: ShareInfo, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    // 🌟 Logic วันที่ยังคงเดิม แต่ปรับสไตล์การแสดงผล
    val displayFutureDateText = remember(data.date) {
        val dateStr = data.date
        if (dateStr.isNullOrBlank()) {
            null
        } else {
            try {
                val datePart = dateStr.substringBefore("T")
                val todayStr = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

                if (datePart > todayStr) {
                    formatThaiDate(datePart)
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp), // 🌟 มาตรฐาน 12.dp
        colors = CardDefaults.cardColors(containerColor = LightSoftWhite),
        border = BorderStroke(1.dp, LightBorder.copy(alpha = 0.3f)), // 🌟 ขอบจางเพื่อความคลีน
    ) {
        Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Box ขนาด 36.dp
            Box(
                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(LightBg),
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
                        "E" -> Res.drawable.ic_form_email_outline
                        "G" -> Res.drawable.ic_nav_social
                        else -> Res.drawable.ic_nav_profile
                    }
                    Icon(painterResource(icon), null, tint = LightPrimary, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = data.name ?: "",
                    style = MaterialTheme.typography.bodyMedium, // 🌟 ใช้ bodyMedium สำหรับชื่อ
                    color = LightText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (data.typeData == "E") {
                    if (displayFutureDateText != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = displayFutureDateText,
                            style = MaterialTheme.typography.labelSmall, // 🌟 ใช้ labelSmall สำหรับวันที่
                            color = Color.Gray
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
                                        .background(LightBg, RoundedCornerShape(200.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val icon = if(data.typeData == "G") Res.drawable.ic_nav_social else Res.drawable.ic_nav_profile
                                    Icon(painterResource(icon), null, tint = LightPrimary, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = data.subText,
                                        color = LightPrimary,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            } else {
                                Spacer(modifier = Modifier.width(1.dp))
                            }

                            if (displayFutureDateText != null) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = displayFutureDateText,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(32.dp)) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_bin),
                    null,
                    tint = RedErr.copy(alpha = 0.7f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color.White,
            shape = RoundedCornerShape(12.dp), // 🌟 ขอบมน 12.dp
            title = {
                Text(
                    text = "ยกเลิกการแชร์",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = LightText
                )
            },
            text = {
                Text(
                    text = "คุณแน่ใจหรือไม่ว่าต้องการยกเลิกการแชร์ข้อมูลนี้ให้ '${data.name ?: "บุคคลนี้"}' ?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = LightText.copy(alpha = 0.7f)
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
                    Text("ปิด", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        )
    }
}