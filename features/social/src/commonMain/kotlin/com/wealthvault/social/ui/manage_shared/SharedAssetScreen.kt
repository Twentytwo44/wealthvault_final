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
import com.wealthvault.domain.social.ShareableItem
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
