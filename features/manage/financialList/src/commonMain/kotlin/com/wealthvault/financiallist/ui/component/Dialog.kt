package com.wealthvault.financiallist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_bin
import com.wealthvault.core.generated.resources.ic_common_pen
import com.wealthvault.core.generated.resources.ic_dashboard_share
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.RedErr
import org.jetbrains.compose.resources.painterResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import com.wealthvault.core.theme.LightSoftWhite

@Composable
fun DetailDialog(
    subtitle: String = "",
    title: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onShare: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // เรายังจำเป็นต้องใช้เพื่อคุมขนาดเอง
        )
    ) {
        // 🌟 1. ใช้ Box ครอบให้เต็มจอ เพื่อทำหน้าที่เป็น "พื้นที่นอกกล่อง"
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null // ปิด Effect แสงเวลาคลิกพื้นหลัง
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            // 🌟 2. ใช้ Surface สำหรับตัวกล่องจริง
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .fillMaxHeight(0.8f)
                    .wrapContentHeight()
                    // 🌟 สำคัญ: ต้องใส่ clickable ทับที่ตัวกล่องไว้ด้วย
                    // เพื่อป้องกันไม่ให้การคลิก "ข้างในกล่อง" ทะลุไปหา Box ตัวนอกแล้วปิดตัวเอง
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        /* ไม่ต้องทำอะไร ป้องกันการคลิกทะลุ */
                    },
                shape = RoundedCornerShape(24.dp),
                color = LightSoftWhite,
                shadowElevation = 12.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // ... (เนื้อหา Header, Content, Footer เดิมของคุณ Champ) ...

                    // --- 1. Fixed Header ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 16.dp)
                    ) {
                        if (subtitle.isNotEmpty()) {
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9E918B),
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF3A2F2A),
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = LightBorder, thickness = 1.dp)
                    }

                    // --- 2. Scrollable Content ---
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 24.dp)
                    ) {
                        content()
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // --- 3. Fixed Footer ---
                    Column {
                        HorizontalDivider(color = LightBorder, thickness = 1.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth().height(70.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // ปุ่มลบ
                            TextButton(
                                onClick = { onDelete(); onDismiss() },
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(painter = painterResource(Res.drawable.ic_common_bin), contentDescription = "ลบ", tint = RedErr, modifier = Modifier.size(20.dp))
                                    Text(text = "ลบ", fontSize = 12.sp, color = RedErr, fontWeight = FontWeight.Medium)
                                }
                            }

                            VerticalDivider(modifier = Modifier.fillMaxHeight(0.6f), color = LightBorder, thickness = 1.dp)

                            // ปุ่มแก้ไข
                            TextButton(
                                onClick = { onEdit(); onDismiss() },
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(painter = painterResource(Res.drawable.ic_common_pen), contentDescription = "แก้ไข", tint = LightPrimary, modifier = Modifier.size(20.dp))
                                    Text(text = "แก้ไข", fontSize = 12.sp, color = LightPrimary, fontWeight = FontWeight.Medium)
                                }
                            }

                            VerticalDivider(modifier = Modifier.fillMaxHeight(0.6f), color = LightBorder, thickness = 1.dp)

                            // ปุ่มแชร์
                            TextButton(
                                onClick = { onShare() },
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(painter = painterResource(Res.drawable.ic_dashboard_share), contentDescription = "แชร์", tint = Color(0xFF8BAAFB), modifier = Modifier.size(20.dp))
                                    Text(text = "แชร์", fontSize = 12.sp, color = Color(0xFF8BAAFB), fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}



// 🌟 DetailRow คงไว้เหมือนเดิมได้เลยครับ สวยอยู่แล้ว!
@Composable
fun DetailRow(
    label: String,
    value: String,
    isHighlight: Boolean = false,
    isLast: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9E918B),
            letterSpacing = 0.4.sp
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isHighlight) Color(0xFFC27A5A) else Color(0xFF3A2F2A)
        )
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 10.dp),
                color = LightBorder,
                thickness = 0.8.dp
            )
        }
    }
}