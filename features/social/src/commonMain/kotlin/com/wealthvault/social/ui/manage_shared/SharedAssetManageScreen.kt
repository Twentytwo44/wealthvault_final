package com.wealthvault.social.ui.manage_shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.share_api.model.ShareGroupData
import com.wealthvault.social.ui.components.space.SharedAssetItem
import com.wealthvault.social.ui.components.space.SpaceTopBar
import com.wealthvault.core.utils.getScreenModel // 🌟 Import getScreenModel มาใช้

class SharedAssetManageScreen(
    private val targetId: String,
    private val targetName: String,
    private val isGroup: Boolean
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        // 🌟 1. ดึง ScreenModel มาใช้
        val screenModel = getScreenModel<SharedAssetManageScreenModel>()

        // 🌟 2. ติดตาม State จาก ScreenModel
        val assetList by screenModel.assetList.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()

        // 🌟 3. โหลดข้อมูลเมื่อเปิดหน้าจอ
        LaunchedEffect(targetId) {
            screenModel.fetchSharedAssets(targetId, isGroup)
        }

        SharedAssetManageContent(
            targetName = targetName,
            isGroup = isGroup,
            assetList = assetList,
            isLoading = isLoading,
            onBackClick = { navigator.pop() },
            onUnShareClick = { assetId ->
                // 🌟 4. ส่งคำสั่งลบไปให้ ScreenModel จัดการ
                screenModel.unShareAsset(assetId, isGroup)
            }
        )
    }
}

// 🌟 ตัว UI หลัก (เบาและสะอาดขึ้นเยอะครับ)
@Composable
fun SharedAssetManageContent(
    targetName: String,
    isGroup: Boolean,
    assetList: List<ShareGroupData>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onUnShareClick: (String) -> Unit // รับ Event การกดลบ
) {
    val themeColor = Color(0xFFC27A5A)
    var openedAssetId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 20.dp)
    ) {
        SpaceTopBar(title = "การจัดการทรัพย์สิน", onBackClick = onBackClick)
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                item {
                    Text(
                        text = "ทรัพย์สินที่คุณแชร์",
                        fontSize = 16.sp,
                        color = themeColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (assetList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            Text(text = "คุณยังไม่ได้แชร์ทรัพย์สินใดๆ", color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                } else {
                    itemsIndexed(
                        items = assetList,
                        key = { _, asset -> asset.groupItemId ?: asset.hashCode() }
                    ) { index, asset ->
                        val assetId = asset.groupItemId ?: ""
                        val assetName = asset.assetDetail?.name ?: "ไม่ระบุชื่อ"
                        val assetType = asset.type ?: ""
                        val imageUrl = asset.assetDetail?.image
                        val assetValue = asset.assetDetail?.amount

                        SharedAssetItem(
                            assetId = assetId,
                            assetName = assetName,
                            assetType = assetType,
                            imageUrl = imageUrl,
                            value = assetValue,
                            showDelete = true,
                            isFirstItem = (index == 0),
                            themeColor = themeColor,

                            isOpened = (openedAssetId == assetId),
                            onOpenRequested = { openedAssetId = assetId },
                            onCloseRequested = { if (openedAssetId == assetId) openedAssetId = null },

                            // 🌟 5. พอกดปุ่มลบ ก็เรียกฟังก์ชันส่งต่อ ID ไปให้ข้างบน
                            onDeleteClick = {
                                onUnShareClick(assetId)
                                openedAssetId = null // รีเซ็ตการ์ดที่เปิดอยู่ให้หดกลับด้วย
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}