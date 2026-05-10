package com.wealthvault.social.ui.manage_shared

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.share_api.model.ShareGroupData
import com.wealthvault.social.ui.components.space.SharedAssetItem
import com.wealthvault.social.ui.components.space.SpaceTopBar

class SharedAssetManageScreen(
    private val targetId: String,
    private val targetName: String,
    private val isGroup: Boolean
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel = getScreenModel<SharedAssetManageScreenModel>()

        val assetList by screenModel.assetList.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()

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
                screenModel.unShareAsset(assetId, isGroup)
            }
        )
    }
}

@Composable
fun SharedAssetManageContent(
    targetName: String,
    isGroup: Boolean,
    assetList: List<ShareGroupData>,
    isLoading: Boolean,
    onBackClick: () -> Unit,
    onUnShareClick: (String) -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    var openedAssetId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.background(LightBg)
            .fillMaxSize()
            .statusBarsPadding()
            .padding(top = 24.dp)
    ) {
        SpaceTopBar(title = "การจัดการทรัพย์สิน", onBackClick = onBackClick)
        HorizontalDivider(color = themeColor.copy(alpha = 0.3f), thickness = 1.dp)

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth().padding(top = 14.dp)) {
                item {
                    Text(
                        text = "ทรัพย์สินที่คุณแชร์",
                        style = MaterialTheme.typography.bodyLarge,
                        color = themeColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (assetList.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
                            Text(text = "คุณยังไม่ได้แชร์ทรัพย์สินใดๆ", color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                } else {
                    itemsIndexed(
                        items = assetList,
                        key = { _, asset -> asset.groupItemId ?: asset.hashCode() }
                    ) { index, asset ->
                        val assetId = asset.groupItemId ?: ""
                        val assetType = asset.type ?: ""
                        val imageUrl = asset.assetDetail?.image
                        val sharedAt = asset.sharedAt

                        // 🌟 1. ดักจับชื่อ: ถ้า name เป็นค่าว่าง ("") ให้ลองใช้ชื่อบริษัทประกันแทน ถ้าไม่มีอีกค่อยขึ้น "ไม่ระบุชื่อ"
                        val assetName = asset.assetDetail?.name?.takeIf { it.isNotBlank() }
                            ?: asset.assetDetail?.companyName?.takeIf { it.isNotBlank() }
                            ?: "ไม่ระบุชื่อ"

                        // 🌟 2. ดักจับยอดเงิน: ให้ครอบคลุมทั้ง ทรัพย์สินทั่วไป, หนี้สิน, และ ประกัน
                        val assetValue = asset.assetDetail?.amount
                            ?: asset.assetDetail?.principal
                            ?: asset.assetDetail?.coverageAmount

                        SharedAssetItem(
                            assetId = assetId,
                            assetName = assetName,
                            assetType = assetType,
                            imageUrl = imageUrl,
                            value = assetValue, // 🌟 โยนยอดเงินที่ดักครบทุกเคสแล้วเข้าไป
                            sharedAt = sharedAt,
                            showDelete = true,
                            isFirstItem = (index == 0),
                            themeColor = themeColor,
                            isOpened = (openedAssetId == assetId),
                            onOpenRequested = { openedAssetId = assetId },
                            onCloseRequested = { if (openedAssetId == assetId) openedAssetId = null },
                            onDeleteClick = {
                                onUnShareClick(assetId)
                                openedAssetId = null
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}