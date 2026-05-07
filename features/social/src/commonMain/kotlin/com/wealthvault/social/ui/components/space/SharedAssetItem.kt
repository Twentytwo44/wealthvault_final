package com.wealthvault.social.ui.components.space

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_asset_type_account
import com.wealthvault.core.generated.resources.ic_asset_type_building
import com.wealthvault.core.generated.resources.ic_asset_type_cash
import com.wealthvault.core.generated.resources.ic_asset_type_insurance
import com.wealthvault.core.generated.resources.ic_asset_type_investment
import com.wealthvault.core.generated.resources.ic_asset_type_land
import com.wealthvault.core.generated.resources.ic_asset_type_loan
import com.wealthvault.core.generated.resources.ic_common_clock
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.formatThaiDate
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun SharedAssetItem(
    assetId: String,
    assetName: String,
    assetType: String,
    imageUrl: String? = null,
    value: Double? = null,
    sharedAt: String? = null,
    showDelete: Boolean = false,
    isFirstItem: Boolean = false,
    isOpened: Boolean = false,
    onOpenRequested: () -> Unit = {},
    onCloseRequested: () -> Unit = {},
    themeColor: Color = Color(0xFFC27A5A),
    onDeleteClick: () -> Unit = {}
) {
    val buttonWidthDp = 80.dp
    val density = LocalDensity.current
    val maxSwipePx = with(density) { buttonWidthDp.toPx() }
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(isOpened) {
        if (!isOpened && offsetX.value != 0f) {
            offsetX.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
        }
    }

    LaunchedEffect(isFirstItem, showDelete) {
        if (isFirstItem && showDelete && !isOpened) {
            kotlinx.coroutines.delay(600)
            offsetX.animateTo(
                targetValue = -maxSwipePx * 0.4f,
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            )
            offsetX.animateTo(
                targetValue = 0f,
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium)
            )
        }
    }

    val thaiAssetType = when {
        assetType.contains("account", ignoreCase = true) -> "เงินฝาก"
        assetType.contains("cash", ignoreCase = true) -> "เงินสด/ทอง"
        assetType.contains("investment", ignoreCase = true) -> "ลงทุน"
        assetType.contains("insurance", ignoreCase = true) -> "ประกัน"
        assetType.contains("building", ignoreCase = true) -> "บ้าน/อาคาร"
        assetType.contains("land", ignoreCase = true) -> "ที่ดิน"
        assetType.contains("loan", ignoreCase = true) || assetType.contains("liability", ignoreCase = true) -> "หนี้สิน"
        assetType.contains("expense", ignoreCase = true) -> "ค่าใช้จ่าย"
        else -> assetType.take(6)
    }
    val isFutureShare = remember(sharedAt) {
        if (sharedAt.isNullOrBlank()) {
            false
        } else {
            try {
                val datePart = sharedAt.substringBefore("T")
                val todayStr = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
                datePart > todayStr
            } catch (e: Exception) {
                false
            }
        }
    }
    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)) {

        // --- สีแดงด้านหลัง (ปุ่มลบ) ---
        if (showDelete) {
            Surface(
                color = Color(0xFFE53935),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.matchParentSize().offset {
                    val safeX = if (offsetX.value.isNaN()) 0f else offsetX.value
                    IntOffset(safeX.coerceAtLeast(0f).roundToInt(), 0)
                }
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().clickable {
                        onDeleteClick()
                        coroutineScope.launch { offsetX.animateTo(0f, tween(300)) }
                    },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(text = "ลบ", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(end = 28.dp))
                }
            }
        }

        // --- การ์ดสีขาวด้านหน้า ---
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 1.dp,
            border = BorderStroke(1.dp, Color(0xFFF3F3F3)), // เพิ่มขอบบางๆ ให้ดูมีมิติเหมือนในรูป
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val safeX = if (offsetX.value.isNaN()) 0f else offsetX.value
                    IntOffset(safeX.roundToInt(), 0)
                }
                .then(
                    if (showDelete) {
                        Modifier.pointerInput(Unit) {
                            detectHorizontalDragGestures(
                                onDragStart = { onOpenRequested() },
                                onDragEnd = {
                                    coroutineScope.launch {
                                        if (offsetX.value < -maxSwipePx / 2) {
                                            offsetX.animateTo(-maxSwipePx, tween(300))
                                        } else {
                                            offsetX.animateTo(0f, tween(300))
                                            onCloseRequested()
                                        }
                                    }
                                },
                                onHorizontalDrag = { change, dragAmount ->
                                    change.consume()
                                    coroutineScope.launch {
                                        val newOffset = (offsetX.value + dragAmount).coerceIn(-maxSwipePx, 0f)
                                        offsetX.snapTo(newOffset)
                                    }
                                }
                            )
                        }
                    } else Modifier
                )
        ) {
            // 🌟 Layout ใหม่ จัดกลุ่ม 3 ส่วน ซ้าย กลาง ขวา
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 🔹 ส่วนที่ 1: ไอคอน (ซ้ายสุด)
                val isImageUrl = imageUrl?.let { it.endsWith(".png", ignoreCase = true) || it.endsWith(".jpg", ignoreCase = true) || it.endsWith(".jpeg", ignoreCase = true) || it.endsWith(".webp", ignoreCase = true) } == true
                if (isImageUrl) {
                    AsyncImage(model = imageUrl, contentDescription = assetName, contentScale = ContentScale.Crop, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)))
                } else {
                    val iconRes = when {
                        assetType.contains("account", ignoreCase = true) -> Res.drawable.ic_asset_type_account
                        assetType.contains("building", ignoreCase = true) -> Res.drawable.ic_asset_type_building
                        assetType.contains("cash", ignoreCase = true) -> Res.drawable.ic_asset_type_cash
                        assetType.contains("insurance", ignoreCase = true) -> Res.drawable.ic_asset_type_insurance
                        assetType.contains("investment", ignoreCase = true) -> Res.drawable.ic_asset_type_investment
                        assetType.contains("land", ignoreCase = true) -> Res.drawable.ic_asset_type_land
                        assetType.contains("loan", ignoreCase = true) || assetType.contains("liability", ignoreCase = true) -> Res.drawable.ic_asset_type_loan
                        else -> null
                    }

                    Box(
                        modifier = Modifier.size(48.dp).background(LightBg, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (iconRes != null) Icon(painterResource(iconRes), contentDescription = assetType, tint = themeColor, modifier = Modifier.size(24.dp))
                        else Text(text = assetType.take(3).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = themeColor)
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // 🔹 ส่วนที่ 2: ชื่อและมูลค่า (ตรงกลาง)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = assetName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF3A2F2A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (value != null) "${formatAmount(value)} บาท" else "ไม่ระบุมูลค่า",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 🔹 ส่วนที่ 3: ป้าย Tag และ วันที่ (ขวาสุด)
                Column(horizontalAlignment = Alignment.End) {
                    // ป้ายประเภท (Pill Tag)
                    Surface(color = themeColor.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)) {
                        Text(
                            text = thaiAssetType,
                            color = themeColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 0.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // วันที่ที่แชร์ (พร้อม Icon นาฬิกา)
//                    if (!sharedAt.isNullOrBlank()) {
                    if (isFutureShare && !sharedAt.isNullOrBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(Res.drawable.ic_common_clock),
                                contentDescription = null,
                                tint = themeColor.copy(alpha = 0.8f),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = formatThaiDate(sharedAt.substringBefore("T")),
                                fontSize = 11.sp,
                                color = themeColor.copy(alpha = 0.8f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}