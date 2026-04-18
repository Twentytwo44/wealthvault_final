package com.wealthvault.core.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.*
import com.wealthvault.core.model.HasImageUrl
import com.wealthvault.core.theme.*
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun DetailDialog(
    subtitle: String = "",
    title: String,
    updatedAt: String = "",
    themeType: String,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onEdit: () -> Unit = {},
    onShare: () -> Unit = {},
    showBottomMenu: Boolean = false,
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
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
                            Column(modifier = Modifier.weight(1f)) {
                                if (subtitle.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = subtitle,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = LightMuted.copy(0.8f)
                                        )
                                        if (updatedAt.isNotEmpty()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    painter = painterResource(Res.drawable.ic_common_update),
                                                    contentDescription = "Last Updated",
                                                    tint = LightMuted.copy(0.7f),
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = updatedAt,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium,
                                                    color = LightMuted.copy(0.8f)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = LightText
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

                        if (showBottomMenu) {
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
                        } else {
                            TextButton(
                                onClick = onDismiss,
                                modifier = Modifier.fillMaxWidth().height(60.dp),
                                shape = RoundedCornerShape(0.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = "ปิด",
                                    fontSize = 16.sp,
                                    color = LightPrimary,
                                    fontWeight = FontWeight.Bold
                                )
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
            color = LightMuted.copy(0.8f),
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

    if (images.isNotEmpty()) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "รูปภาพเอกสาร / สมุดบัญชี",
            style = MaterialTheme.typography.labelSmall,
            color = LightMuted.copy(0.8f),
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
                            .clickable { selectedImageIndex = index }
                    )
                }
            }
        }
    }

    if (documents.isNotEmpty()) {
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "ไฟล์แนบ",
            style = MaterialTheme.typography.labelSmall,
            color = LightMuted.copy(0.8f),
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
                    Box(
                        modifier = Modifier
                            .size(width = 40.dp, height = 24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(LightPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = extension,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = LightPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = fileNameOnly,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A2F2A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f).padding(end = 10.dp)
                    )

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
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    ZoomableImage(
                        url = images[page],
                        onDismiss = { selectedImageIndex = null }
                    )
                }

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
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        val zoom = event.calculateZoom()
                        val pan = event.calculatePan()

                        scale = (scale * zoom).coerceIn(1f, 4f)

                        if (scale > 1f) {
                            val displayWidth = size.width.toFloat()
                            val displayHeight = size.height.toFloat()
                            val maxX = (displayWidth * (scale - 1)) / 2f
                            val maxY = (displayHeight * (scale - 1)) / 2f

                            offset = Offset(
                                x = (offset.x + pan.x).coerceIn(-maxX, maxX),
                                y = (offset.y + pan.y).coerceIn(-maxY, maxY)
                            )
                            event.changes.forEach { it.consume() }
                        } else {
                            offset = Offset.Zero
                            if (zoom != 1f) {
                                event.changes.forEach { it.consume() }
                            }
                        }
                    } while (event.changes.any { it.pressed })
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
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3A2F2A)
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
                    color = Color(0xFF9E918B),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    )
}