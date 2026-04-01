package com.wealthvault.financiallist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightDebt
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSecondary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.RedErr
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.wealthvault.core.generated.resources.ic_form_cross
import com.wealthvault.core.model.HasImageUrl
import com.wealthvault.core.theme.LightMuted
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
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
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(color = LightBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth().height(70.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
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
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9E918B),
            letterSpacing = 0.4.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value.ifEmpty { "-" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (isHighlight) Color(0xFFC27A5A) else Color(0xFF3A2F2A)
        )
        if (!isLast) {
            HorizontalDivider(
                modifier = Modifier.padding(top = 14.dp),
                color = LightBorder.copy(alpha = 0.5f),
                thickness = 0.8.dp
            )
        }
    }
}


@Composable
fun DetailImageRow(files: List<Any>?) {
    // 🌟 1. สร้าง State สำหรับเก็บ URL ของรูปที่จะโชว์เต็มจอ (ถ้าเป็น null คือไม่ได้เปิด)
    var fullScreenImageUrl by remember { mutableStateOf<String?>(null) }

    // ดึง URL ออกมาทั้งหมด
    val imageUrls = files?.mapNotNull { file ->
        when (file) {
            is HasImageUrl -> file.url
            is String -> file
            else -> null
        }
    } ?: emptyList()

    // 🌟 ส่วนแสดงผลแถบรูปภาพแนวนอน
    if (imageUrls.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "รูปภาพเอกสาร / สมุดบัญชี",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9E918B),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            imageUrls.forEach { url ->
                if (url.isNotEmpty()) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Document Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(140.dp)
                            .height(140.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            .clickable {
                                // 🌟 2. เมื่อกดที่รูป ให้เซ็ตค่า URL ลงใน State เพื่อเปิดเต็มจอ
                                fullScreenImageUrl = url
                            }
                    )
                }
            }
        }
    }

    // 🌟 3. ส่วนแสดงผลรูปเต็มจอ (จะโชว์ก็ต่อเมื่อ fullScreenImageUrl มีค่า)
    fullScreenImageUrl?.let { url ->
        Dialog(
            onDismissRequest = { fullScreenImageUrl = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false, // บังคับให้ Dialog กว้างเต็มจอขอบชนขอบ
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f)) // พื้นหลังสีดำโปร่งแสงนิดๆ
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        // กดพื้นที่ว่างสีดำเพื่อปิด
                        fullScreenImageUrl = null
                    },
                contentAlignment = Alignment.Center
            ) {
                // รูปภาพขนาดเต็ม
                AsyncImage(
                    model = url,
                    contentDescription = "Fullscreen Image",
                    contentScale = ContentScale.Fit, // ใช้ Fit เพื่อไม่ให้รูปโดนตัด
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp) // เว้นขอบนิดหน่อยให้สวยงาม
                )
            }
        }
    }
}