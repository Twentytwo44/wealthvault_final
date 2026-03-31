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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightSecondary
import com.wealthvault.core.theme.LightSoftWhite

@Composable
fun DetailDialog(
    subtitle: String = "",
    title: String,
    themeType: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onShare: () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onDismiss()
                },
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.88f)
                    .fillMaxHeight(0.8f)
                    .wrapContentHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {},
                shape = RoundedCornerShape(24.dp),
                color = LightSoftWhite,
                shadowElevation = 12.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {

                    // --- 1. Fixed Header ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(LightBg)
                            .padding(top = 24.dp, bottom = 16.dp, start = 24.dp, end = 24.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(5.dp)
                                    .height(if (subtitle.isNotEmpty()) 36.dp else 24.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (themeType == "asset") {
                                            Brush.linearGradient(colors = listOf(LightAsset, LightSecondary))
                                        } else {
                                            Brush.linearGradient(colors = listOf(LightDebt, LightSecondary))
                                        }
                                    )
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                if (subtitle.isNotEmpty()) {
                                    Text(
                                        text = subtitle,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF9E918B)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF3A2F2A)
                                )
                            }
                        }
                    }

                    // 🌟 ปรับเส้นกั้น: ใช้ .copy(alpha = 0.5f) เพื่อให้เส้นดูจางและละมุนขึ้น ไม่แข็งเกินไป
                    HorizontalDivider(color = LightBorder.copy(alpha = 0.5f), thickness = 0.8.dp)

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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        HorizontalDivider(color = LightBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth().height(70.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 🌟 ซ้าย: ปุ่มลบ (อันตรายสุด ไว้ซ้ายสุด)
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

                            VerticalDivider(modifier = Modifier.fillMaxHeight(0.5f), color = LightBorder.copy(alpha = 0.5f), thickness = 0.8.dp)

                            // 🌟 กลาง: ปุ่มแชร์ (สลับมาไว้ตรงกลาง)
                            TextButton(
                                onClick = { onShare() },
                                modifier = Modifier.weight(1f).fillMaxHeight(),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Icon(painter = painterResource(Res.drawable.ic_dashboard_share), contentDescription = "แชร์", tint = LightPrimary, modifier = Modifier.size(20.dp))
                                    Text(text = "แชร์", fontSize = 12.sp, color = LightPrimary, fontWeight = FontWeight.Medium)
                                }
                            }

                            VerticalDivider(modifier = Modifier.fillMaxHeight(0.5f), color = LightBorder.copy(alpha = 0.5f), thickness = 0.8.dp)

                            // 🌟 ขวา: ปุ่มแก้ไข (ใช้บ่อยสุด ไว้ขวาสุดให้กดง่าย)
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
                        }
                    }
                }
            }
        }
    }
}


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
            .padding(vertical = 10.dp) // 🌟 ปรับช่องไฟ: เพิ่ม padding แนวตั้งจาก 6.dp เป็น 10.dp ให้มีพื้นที่หายใจ
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9E918B),
            letterSpacing = 0.4.sp
        )
        Spacer(modifier = Modifier.height(6.dp)) // 🌟 ปรับช่องไฟ: เพิ่มระยะห่างระหว่าง Label กับ Value
        Text(
            text = value.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isHighlight) Color(0xFFC27A5A) else Color(0xFF3A2F2A)
        )
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 14.dp), // 🌟 ปรับช่องไฟ: เพิ่มระยะห่างก่อนขีดเส้นกั้น
                color = LightBorder.copy(alpha = 0.5f), // 🌟 ปรับเส้นกั้น: ให้จางลง
                thickness = 0.8.dp
            )
        }
    }
}