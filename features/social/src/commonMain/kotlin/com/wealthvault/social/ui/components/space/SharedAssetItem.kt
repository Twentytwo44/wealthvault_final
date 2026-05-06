package com.wealthvault.social.ui.components.space

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.utils.formatAmount
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import kotlin.math.roundToInt

@Composable
fun SharedAssetItem(
    assetId: String,          // 🌟 ต้องมี ID เพื่อบอกตัวแม่ว่าใครคือใคร
    assetName: String,
    assetType: String,
    imageUrl: String? = null,
    value: Double? = null,
    showDelete: Boolean = false,
    isFirstItem: Boolean = false,
    isOpened: Boolean = false, // 🌟 สถานะจากตัวแม่ (สั่งเปิด/ปิด)
    onOpenRequested: () -> Unit = {}, // 🌟 ส่งเสียงบอกตัวแม่ตอนเริ่มปัด
    onCloseRequested: () -> Unit = {}, // 🌟 ส่งเสียงบอกตัวแม่ตอนปัดกลับเอง
    themeColor: Color = Color(0xFFC27A5A),
    onDeleteClick: () -> Unit = {}
) {
    val buttonWidthDp = 80.dp
    val density = LocalDensity.current
    val maxSwipePx = with(density) { buttonWidthDp.toPx() }
    val offsetX = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    // 🌟 1. ดักฟังตัวแม่: ถ้าแม่บอกให้ปิด (isOpened = false) และเราเปิดอยู่ ให้หดกลับซะ!
    LaunchedEffect(isOpened) {
        if (!isOpened && offsetX.value != 0f) {
            offsetX.animateTo(0f, tween(300, easing = FastOutSlowInEasing))
        }
    }

    // 🌟 2. แอนิเมชัน Hint ดั้งเดิมของคุณ Champ
    LaunchedEffect(isFirstItem, showDelete) {
        if (isFirstItem && showDelete && !isOpened) { // ไม่ทำถ้ากำลังโดนปัดอยู่
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

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)) {

        // --- สีแดงด้านหลัง ---
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
            shape = RoundedCornerShape(20.dp),
            color = Color.White,
            shadowElevation = 2.dp,
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
                                onDragStart = {
                                    // 🌟 พอเอานิ้วแตะปุ๊บ บอกตัวแม่เลยว่าฉันขอยึดพื้นที่! (ใบอื่นจะได้ปิด)
                                    onOpenRequested()
                                },
                                onDragEnd = {
                                    coroutineScope.launch {
                                        if (offsetX.value < -maxSwipePx / 2) {
                                            offsetX.animateTo(-maxSwipePx, tween(300))
                                        } else {
                                            offsetX.animateTo(0f, tween(300))
                                            onCloseRequested() // ถ้าปัดไม่สุดแล้วหดกลับ บอกตัวแม่ด้วยว่าว่างแล้ว
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                val isImageUrl = imageUrl?.let { it.endsWith(".png", ignoreCase = true) || it.endsWith(".jpg", ignoreCase = true) || it.endsWith(".jpeg", ignoreCase = true) || it.endsWith(".webp", ignoreCase = true) } == true
                if (isImageUrl) {
                    AsyncImage(model = imageUrl, contentDescription = assetName, contentScale = ContentScale.Crop, modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)))
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
                    Box(modifier = Modifier.size(48.dp).background(LightBg, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        if (iconRes != null) Icon(painterResource(iconRes), contentDescription = assetType, tint = LightPrimary, modifier = Modifier.size(28.dp))
                        else Text(text = assetType.take(3).uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = assetName, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(text = if (value != null) "${formatAmount(value)} บาท" else "ไม่ระบุมูลค่า", fontSize = 13.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(color = themeColor.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)) {
                    Text(text = thaiAssetType, color = themeColor, fontSize = 10.sp, fontWeight = FontWeight.Medium, modifier = Modifier.padding(horizontal = 8.dp, vertical = 1.dp))
                }
            }
        }
    }
}