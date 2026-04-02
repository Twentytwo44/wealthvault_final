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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.style.TextOverflow
import com.wealthvault.core.generated.resources.ic_common_doc
import com.wealthvault.core.generated.resources.ic_common_download
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Offset
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.text.AnnotatedString


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
                    Spacer(modifier = Modifier.height(6.dp))

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
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(color = LightBorder.copy(alpha = 0.5f), thickness = 0.8.dp)
                        Row(
                            modifier = Modifier.fillMaxWidth().height(70.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = { onDelete() },
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
            .padding(vertical = 6.dp)
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
    // 🌟 เปลี่ยนจากเก็บ URL เป็นเก็บ Index แทน เพื่อให้รู้ว่ากำลังเปิดรูปที่เท่าไหร่
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    val uriHandler = LocalUriHandler.current

    val images = mutableListOf<String>()
    val documents = mutableListOf<HasImageUrl>()

    files?.forEach { file ->
        when (file) {
            is HasImageUrl -> {
                if (file.fileType.startsWith("image/")) images.add(file.url)
                else documents.add(file)
            }
            is String -> images.add(file)
        }
    }

    // --- 🖼️ ส่วนแสดงผลรูปภาพ ---
    if (images.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "รูปภาพเอกสาร / สมุดบัญชี",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9E918B),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            images.forEachIndexed { index, url ->
                if (url.isNotEmpty()) {
                    AsyncImage(
                        model = url,
                        contentDescription = "Document Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(88.dp)
                            .height(88.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.LightGray.copy(alpha = 0.2f))
                            // 🌟 ตอนกดให้เก็บค่า Index ของรูปนั้น
                            .clickable { selectedImageIndex = index }
                    )
                }
            }
        }
    }

    // --- 📄 ส่วนแสดงผลไฟล์เอกสาร (PDF, อื่นๆ) คงเดิม ---
    if (documents.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "ไฟล์แนบ",
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF9E918B),
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            documents.forEach { doc ->
                val fullName = doc.url.substringAfterLast("/")
                val extension = fullName.substringAfterLast(".", "").uppercase()
                val fileNameOnly = fullName.substringBeforeLast(".")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF8F7F6))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. เปลี่ยนไอคอนเป็นป้ายนามสกุลไฟล์
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightPrimary.copy(alpha = 0.1f)), // สีจางๆ ตามธีมแอป
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = extension, // PDF, DOCX ฯลฯ
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LightPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // 2. ชื่อไฟล์อย่างเดียว (ตัดท้ายถ้ายาวเกิน)
                    Text(
                        text = fileNameOnly,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A2F2A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 10.dp)
                    )

                    // 3. ปุ่มดาวน์โหลด
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_download),
                        contentDescription = null,
                        tint = LightPrimary,
                        modifier = Modifier.size(20.dp).clickable { uriHandler.openUri(doc.url) }
                    )
                }
            }
        }
    }

    // --- 🔍 ส่วนแสดงผลรูปเต็มจอแบบปัดได้และซูมได้ ---
    // --- 🔍 ส่วนแสดงผลรูปเต็มจอแบบปัดได้และซูมได้ ---
    selectedImageIndex?.let { startIndex ->
        val pagerState = rememberPagerState(initialPage = startIndex) { images.size }

        Dialog(
            onDismissRequest = { selectedImageIndex = null },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.9f))
            ) {
                // 1. Pager สำหรับปัดซ้าย-ขวา
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                    // 🌟 ไม่ต้องใช้ userScrollEnabled แล้ว ปล่อยให้มันทำงานอิสระได้เลย
                ) { page ->
                    ZoomableImage(
                        url = images[page],
                        onDismiss = { selectedImageIndex = null }
                    )
                }

                // 2. ตัวนับจำนวนรูป (1/3) แสดงด้านบน
                if (images.size > 1) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${images.size}",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 24.dp)
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // 3. ปุ่มดาวน์โหลดมุมขวาล่าง
                val currentUrl = images[pagerState.currentPage]
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 32.dp, end = 32.dp)
                        .size(48.dp)
                        .clickable { uriHandler.openUri(currentUrl) },
                    shape = CircleShape,
                    color = LightPrimary,
                    shadowElevation = 8.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_common_download),
                            contentDescription = "Download Image",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

// 🌟 Component ใหม่: เขียน Logic การซูมเองเพื่อให้ Pager ทำงานได้
@Composable
fun ZoomableImage(
    url: String,
    onDismiss: () -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // 1. ดักการแตะ 1 ครั้ง (ปิด) และ 2 ครั้ง (ซูมลัด)
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { onDismiss() },
                    onDoubleTap = {
                        if (scale > 1f) {
                            scale = 1f
                            offset = Offset.Zero
                        } else {
                            scale = 2.5f
                        }
                    }
                )
            }
            // 2. 🌟 ดักการลากนิ้วและซูม (Custom Gesture)
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown() // รอให้นิ้วแตะจอ
                    do {
                        val event = awaitPointerEvent()
                        val zoom = event.calculateZoom()
                        val pan = event.calculatePan()

                        scale = (scale * zoom).coerceIn(1f, 4f)

                        // 🌟 หัวใจสำคัญอยู่ตรงนี้ครับ!
                        if (scale > 1f) {
                            // ถ้ารูปใหญ่กว่า 1x ให้คำนวณขอบเขตการเลื่อน
                            val displayWidth = size.width.toFloat()
                            val displayHeight = size.height.toFloat()
                            val maxX = (displayWidth * (scale - 1)) / 2f
                            val maxY = (displayHeight * (scale - 1)) / 2f

                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )

                            // 🛑 สั่ง .consume() เพื่อ "กิน" Event ไว้ ไม่ให้ Pager เอาไปใช้ (ล็อกไม่ให้เปลี่ยนรูป)
                            event.changes.forEach { it.consume() }

                        } else {
                            // ถ้ารูปอยู่ที่ 1x (ขนาดปกติ)
                            offset = Offset.Zero

                            // ถ้ากำลังถ่างนิ้วซูมเข้า-ออก (zoom ไม่เท่ากับ 1) ให้กิน Event ไว้ก่อน
                            if (zoom != 1f) {
                                event.changes.forEach { it.consume() }
                            }
                            // 🟢 นอกเหนือจากนั้น (ลากนิ้วเฉยๆ ตอน 1x) เราไม่ใช้คำสั่ง consume()
                            // ผลคือ Pager จะมองเห็นการลากนิ้วนี้ แล้วทำการ "ปัดเปลี่ยนรูป" ให้เราครับ!
                        }
                    } while (event.changes.any { it.pressed }) // ทำวนไปจนกว่าจะยกนิ้วขึ้น
                }
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = url,
            contentDescription = "Zoomable Image",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                // ใช้ graphicsLayer เพื่อยืดขยายภาพตามตัวแปร
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = offset.x
                    translationY = offset.y
                }
        )
    }
}

@Composable
fun ConfirmDeleteDialog(
    title: String = "ยืนยันการลบ",
    message: Any,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp), // 🌟 โค้งมนเข้ากับธีมแอป
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A2F2A) // LightText
            )
        },
        text = {
            when (message) {
                is AnnotatedString -> Text(text = message)
                is String -> Text(text = message)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                }
            ) {
                Text(
                    text = "ลบ",
                    color = RedErr,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text(
                    text = "ยกเลิก",
                    color = Color(0xFF9E918B), // LightMuted
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}