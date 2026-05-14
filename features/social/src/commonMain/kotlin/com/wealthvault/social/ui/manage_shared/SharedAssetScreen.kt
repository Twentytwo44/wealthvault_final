package com.wealthvault.social.ui.manage_shared

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_asset_type_account
import com.wealthvault.core.generated.resources.ic_asset_type_building
import com.wealthvault.core.generated.resources.ic_asset_type_cash
import com.wealthvault.core.generated.resources.ic_asset_type_insurance
import com.wealthvault.core.generated.resources.ic_asset_type_investment
import com.wealthvault.core.generated.resources.ic_asset_type_land
import com.wealthvault.core.generated.resources.ic_asset_type_loan
import com.wealthvault.core.generated.resources.ic_form_check
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.theme.LightSurface
import com.wealthvault.core.theme.WealthVaultTheme
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.share_api.model.ItemToShareData
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.social.ui.components.space.simpleVerticalScrollbar
import org.jetbrains.compose.resources.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class SharedAssetScreen(
    private val targetId: String,
    private val targetName: String,
    private val isGroup: Boolean
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // 🌟 1. ดึง ScreenModel มาใช้งาน
        val screenModel = getScreenModel<SharedAssetScreenModel>()

        val assetList by screenModel.assetList.collectAsStateWithLifecycle()
        val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
        val isShareSuccess by screenModel.isShareSuccess.collectAsStateWithLifecycle()

        // 🌟 2. โหลดข้อมูลเมื่อเปิดหน้าจอ โดยส่ง targetId และ isGroup ไป
        LaunchedEffect(targetId) {
            screenModel.fetchItemsToShare(targetId, isGroup)
        }

        // 🌟 3. ถ้าบันทึกสำเร็จ ให้เด้งกลับหน้าก่อนหน้า
        LaunchedEffect(isShareSuccess) {
            if (isShareSuccess) {
                navigator.pop()
            }
        }

        WealthVaultTheme {
            SharedAssetContent(
                targetName = targetName,
                assetList = assetList, // 🌟 ส่งข้อมูลจริงจาก API ลงไป
                isLoading = isLoading,
                onBackClick = { navigator.pop() },
                onShareSubmit = { selectedIds ->
                    // 🌟 4. ส่งข้อมูลไปบันทึก (ต้องส่ง isGroup ไปด้วยตามที่เราแก้ใน ScreenModel ล่าสุด)
                    screenModel.submitShareAssets(
                        targetId = targetId,
                        isGroup = isGroup,
                        selectedIds = selectedIds
                    )
                }
            )
        }
    }
}

@Composable
fun SharedAssetContent(
    targetName: String,
    assetList: List<ItemToShareData>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onShareSubmit: (List<String>) -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    val scrollState = rememberScrollState()

    val filteredList = remember(assetList) {
        assetList.filter { it.isShared == false }
    }

    val selectedAssets = remember { mutableStateListOf<String>() }

    LaunchedEffect(filteredList) {
        selectedAssets.clear()
    }

    // 🌟 1. ROOT Column: ใส่ตัวป้องกันแถบด้านบนและแถบ Home ด้านล่างไว้ตรงนี้ที่เดียวจบ!
    Column(
        modifier = Modifier.background(LightBg)
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding() // ป้องกันไม่ให้ปุ่มตกขอบล่าง
    ) {

        // 🌟 2. Column สำหรับเนื้อหาด้านบน + กล่อง Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // กินพื้นที่ที่เหลือทั้งหมด เพื่อ "ดัน" ปุ่มลงไปข้างล่างสุด
                .padding(top = 24.dp)
        ) {
            SpaceTopBar(title = "แชร์ทรัพย์สิน", onBackClick = onBackClick)

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "เลือกสินทรัพย์ที่ต้องการแชร์",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = themeColor,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Text(
                text = "คุณต้องการให้ $targetName เห็นสินทรัพย์ อะไรของคุณบ้าง?",
                style = MaterialTheme.typography.bodyMedium,
                color = themeColor.copy(alpha = 0.8f),
                modifier = Modifier.padding(top = 4.dp, start = 24.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // --- 3. กล่องรายการสินทรัพย์ ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = themeColor)
                    }
                } else if (filteredList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "ไม่มีรายการที่สามารถแชร์ได้",
                            color = Color.Gray,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .simpleVerticalScrollbar(state = scrollState, color = themeColor)
                            .verticalScroll(scrollState)
                    ) {
                        filteredList.forEach { asset ->
                            val assetId = asset.id ?: return@forEach
                            val assetName = asset.name ?: "ไม่ระบุชื่อ"
                            val isChecked = selectedAssets.contains(assetId)

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (isChecked) selectedAssets.remove(assetId)
                                        else selectedAssets.add(assetId)
                                    }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                // --- ส่วนจัดการรูปภาพ/ไอคอน ---
                                val imageUrl = asset.image
                                val isImageUrl = !imageUrl.isNullOrEmpty()

                                if (isImageUrl) {
                                    AsyncImage(
                                        model = imageUrl,
                                        contentDescription = assetName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)),

                                        )
                                } else {
                                    val assetType = asset.type ?: ""
                                    val iconRes = when {
                                        assetType.contains("account", ignoreCase = true) -> Res.drawable.ic_asset_type_account
                                        assetType.contains("building", ignoreCase = true) -> Res.drawable.ic_asset_type_building
                                        assetType.contains("cash", ignoreCase = true) -> Res.drawable.ic_asset_type_cash
                                        assetType.contains("insurance", ignoreCase = true) -> Res.drawable.ic_asset_type_insurance
                                        assetType.contains("investment", ignoreCase = true) -> Res.drawable.ic_asset_type_investment
                                        assetType.contains("land", ignoreCase = true) -> Res.drawable.ic_asset_type_land
                                        assetType.contains("liability", ignoreCase = true) -> Res.drawable.ic_asset_type_loan
                                        else -> null
                                    }

                                    Box(
                                        modifier = Modifier.size(40.dp).background(LightBg, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (iconRes != null) {
                                            Icon(painterResource(iconRes), contentDescription = assetType, tint = LightPrimary, modifier = Modifier.size(20.dp))
                                        } else {
                                            Text(text = assetType.take(3).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(14.dp))

                                // --- ชื่อและราคา ---
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = assetName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF3A2F2A)
                                    )
                                    asset.value?.let { valAmt ->
                                        Text(
                                            text = "${formatAmount(valAmt)} บาท",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = Color.Gray
                                        )
                                    }
                                }

                                // --- Checkbox ---
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(RoundedCornerShape(8.dp)) // ปรับความมนให้เข้ากับธีม
                                        .background(if (isChecked) themeColor else Color.Transparent)
                                        .border(
                                            width = 2.dp,
                                            color = if (isChecked) themeColor else Color.LightGray,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            // 🌟 ลอจิกการจัดการรายการที่เลือก
                                            if (isChecked) {
                                                selectedAssets.remove(assetId)
                                            } else {
                                                selectedAssets.add(assetId)
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isChecked) {
                                        Icon(
                                            painter = painterResource(Res.drawable.ic_form_check),
                                            contentDescription = null,
                                            tint = LightSoftWhite, // หรือ Color.White ตามที่คุณแชมป์ถนัด
                                            modifier = Modifier.size(18.dp) // ปรับขนาดไอคอนให้พอดีกล่อง 24dp
                                        )
                                    }
                                }
                            }

                            if (asset != filteredList.last()) {
                                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f), modifier = Modifier.padding(horizontal = 16.dp))
                            }
                        }
                    }
                }
            }
        }

        // 🌟 4. ปุ่มแบ่งปันข้อมูล (หลุดจาก weight มาอยู่เดี่ยวๆ ด้านล่างสุด)
        Button(
            onClick = { onShareSubmit(selectedAssets.toList()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 24.dp, bottom = 24.dp) // เพิ่มระยะห่างบนล่างให้ดูโปร่งขึ้น
                .height(46.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
            shape = RoundedCornerShape(12.dp),
            enabled = !isLoading && selectedAssets.isNotEmpty()
        ) {
            Text(text = "แบ่งปันข้อมูล", style = MaterialTheme.typography.bodyLarge, color = LightSurface)
        }
    }
}
