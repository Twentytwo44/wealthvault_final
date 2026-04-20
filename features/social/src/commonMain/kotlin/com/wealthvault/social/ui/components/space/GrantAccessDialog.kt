package com.wealthvault.social.ui.components.space

import androidx.compose.foundation.ScrollState
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.share_api.model.ShareGroupData
import com.wealthvault.social.data.SocialRepositoryImpl
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_asset_type_account
import com.wealthvault.core.generated.resources.ic_asset_type_building
import com.wealthvault.core.generated.resources.ic_asset_type_cash
import com.wealthvault.core.generated.resources.ic_asset_type_expense
import com.wealthvault.core.generated.resources.ic_asset_type_insurance
import com.wealthvault.core.generated.resources.ic_asset_type_investment
import com.wealthvault.core.generated.resources.ic_asset_type_land
import com.wealthvault.core.generated.resources.ic_asset_type_loan
import com.wealthvault.core.theme.LightBg
import org.jetbrains.compose.resources.painterResource

// ===============================================
// 🌟 Component ใหม่: การ์ดจัดการการแชร์ (ฝังในแชท)
// ===============================================
data class MockAssetData(val id: String, val name: String)

@Composable
fun InlineGrantAccessCard(
    groupId: String,
    targetName: String,
    targetUserId: String,
    themeColor: Color,
    repository: SocialRepositoryImpl = koinInject(),
    onSaveSuccess: (List<String>) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var assetList by remember { mutableStateOf<List<ShareGroupData>>(emptyList()) }
    val selectedAssets = remember { mutableStateListOf<String>() }
    val mocID = "844c4180-4c6a-438e-bfd3-0e78a24ec1b1"

    val scrollState = rememberScrollState()

    LaunchedEffect(groupId) {
        isLoading = true
        val result = repository.getShareGroupItems(groupId)
        val allItems = result.getOrNull() ?: emptyList()
        assetList = allItems.filter { it.sharedBy == mocID }
        isLoading = false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ต้องการแชร์ asset ของคุณให้ $targetName\nในช่วงก่อนที่ $targetName จะเข้ากลุ่มหรือไม่",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = themeColor)
                    }
                } else if (assetList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(80.dp), contentAlignment = Alignment.Center) {
                        Text("ไม่มีรายการทรัพย์สินที่สามารถแชร์ได้", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 240.dp)
                            .simpleVerticalScrollbar(state = scrollState, color = themeColor)
                            .verticalScroll(scrollState)
                    ) {
                        assetList.forEach { asset ->
                            val assetId = asset.groupItemId ?: return@forEach
                            val assetName = asset.assetDetail?.name ?: "ไม่ระบุชื่อ"
                            val isChecked = selectedAssets.contains(assetId)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 4.dp, end = 8.dp)
                                    .clickable {
                                        if (isChecked) selectedAssets.remove(assetId)
                                        else selectedAssets.add(assetId)
                                    }
                            ) {
                                // 🌟 ส่วนจัดการรูปภาพ
                                val imageUrl = asset.assetDetail?.image
                                // เช็คว่ามี URL และลงท้ายด้วยนามสกุลไฟล์รูปภาพหรือไม่
                                val isImageUrl = imageUrl?.let {
                                    it.endsWith(".png", ignoreCase = true) ||
                                            it.endsWith(".jpg", ignoreCase = true) ||
                                            it.endsWith(".jpeg", ignoreCase = true) ||
                                            it.endsWith(".webp", ignoreCase = true)
                                } == true

                                if (isImageUrl) {
                                    // 🌟 โหลดรูปจริงด้วย Coil
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = assetName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )
                                } else {
                                    // 🌟 ดึงค่า type ออกมาเช็ค
                                    val assetType = asset.type ?: ""

                                    // 🌟 จับคู่คำใน type กับรูป Icon ของคุณ Champ
                                    val iconRes = when {
                                        assetType.contains("account", ignoreCase = true) -> Res.drawable.ic_asset_type_account
                                        assetType.contains("building", ignoreCase = true) -> Res.drawable.ic_asset_type_building
                                        assetType.contains("cash", ignoreCase = true) -> Res.drawable.ic_asset_type_cash
                                        assetType.contains("expense", ignoreCase = true) -> Res.drawable.ic_asset_type_expense
                                        assetType.contains("insurance", ignoreCase = true) -> Res.drawable.ic_asset_type_insurance
                                        assetType.contains("investment", ignoreCase = true) -> Res.drawable.ic_asset_type_investment
                                        assetType.contains("land", ignoreCase = true) -> Res.drawable.ic_asset_type_land
                                        assetType.contains("loan", ignoreCase = true) || assetType.contains("liability", ignoreCase = true) -> Res.drawable.ic_asset_type_loan
                                        else -> null // ถ้าไม่ตรงกับอะไรเลย ให้เป็น null ไว้ไปโชว์ตัวหนังสือแทน
                                    }

                                    // 🌟 วาดกล่องใส่ Icon
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(LightBg, RoundedCornerShape(8.dp)), // ปรับพื้นหลังให้อ่อนลงนิดนึงจะได้เห็น Icon ชัดๆ
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (iconRes != null) {
                                            // ถ้าจับคู่ Icon เจอ ให้โชว์ Icon
                                            Icon(
                                                painter = painterResource(iconRes),
                                                contentDescription = assetType,
                                                tint = LightPrimary, // 💡 ใช้สี themeColor ให้เข้ากับธีมของแชท หรือถ้าอยากได้สีเทาเปลี่ยนเป็น Color.DarkGray ได้ครับ
                                                modifier = Modifier.size(24.dp) // ย่อขนาด Icon ลงนิดนึงให้อยู่ในกรอบ 36dp ได้พอดี
                                            )
                                        } else {
                                            // 🌟 กันเหนียว: ถ้าเกิดมี type แปลกๆ โผล่มาที่ไม่มี Icon ให้กลับมาโชว์ตัวหนังสือ 4 ตัวแรกเหมือนเดิม
                                            Text(
                                                text = assetType.take(4).uppercase(),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.DarkGray,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Text(
                                    text = assetName,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    modifier = Modifier.weight(1f)
                                )

                                Checkbox(
                                    checked = isChecked,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedAssets.add(assetId)
                                        else selectedAssets.remove(assetId)
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = themeColor)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (selectedAssets.size != assetList.size) {
                        Text(
                            text = "เลือกทั้งหมด",
                            color = themeColor,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .clickable {
                                    if (selectedAssets.size != assetList.size) {
                                        selectedAssets.clear()
                                        selectedAssets.addAll(assetList.mapNotNull { it.groupItemId })
                                    }
                                }
                                .padding(4.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        }
                        if (selectedAssets.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ล้าง",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .clickable {
                                        selectedAssets.clear()
                                    }
                                    .padding(4.dp)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onSaveSuccess(selectedAssets)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("บันทึก", color = Color.White)
                    }
                }
            }
        }
    }
}

// ... Modifier.simpleVerticalScrollbar และ SystemAlertBubble คงเดิมครับ ...
// ===============================================
// 🌟 Modifier พิเศษสำหรับวาด Scrollbar แสนสวย
// ===============================================
fun Modifier.simpleVerticalScrollbar(
    state: ScrollState,
    width: androidx.compose.ui.unit.Dp = 4.dp,
    color: Color
): Modifier = drawWithContent {
    drawContent()
    val viewportHeight = size.height
    val maxScroll = state.maxValue.toFloat()

    // วาด Scrollbar ก็ต่อเมื่อเนื้อหามันล้นจอ (maxScroll > 0)
    if (maxScroll > 0f) {
        val totalHeight = viewportHeight + maxScroll
        val scrollbarHeight = (viewportHeight / totalHeight) * viewportHeight
        val scrollOffsetRatio = state.value.toFloat() / maxScroll
        val scrollbarOffsetY = scrollOffsetRatio * (viewportHeight - scrollbarHeight)

        drawRoundRect(
            color = color.copy(alpha = 0.6f), // 🌟 ปรับโปร่งแสงนิดนึงให้ดูหรูหรา
            topLeft = Offset(size.width - width.toPx(), scrollbarOffsetY),
            size = Size(width.toPx(), scrollbarHeight),
            cornerRadius = CornerRadius(width.toPx() / 2, width.toPx() / 2)
        )
    }
}

@Composable
fun SystemAlertBubble(content: String) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
        ) {
            Text(
                text = content,
                color = LightPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}