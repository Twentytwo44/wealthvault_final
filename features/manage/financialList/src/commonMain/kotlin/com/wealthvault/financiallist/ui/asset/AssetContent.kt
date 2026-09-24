package com.wealthvault.financiallist.ui.asset

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.navigator.Navigator
import com.wealthvault.core.components.ConfirmDeleteDialog
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_plus
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.theme.LightAsset
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.domain.portfolio.AccountData
import com.wealthvault.domain.portfolio.GetBuildingData
import com.wealthvault.domain.portfolio.GetCashData
import com.wealthvault.domain.portfolio.GetInsuranceData
import com.wealthvault.domain.portfolio.GetInvestmentData
import com.wealthvault.domain.portfolio.GetLandData
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.SmartAssetDetailDialog
import com.wealthvault.financiallist.ui.shareasset.ShareAssetScreen
import org.jetbrains.compose.resources.painterResource

@Composable
fun AssetContent(
    screenModel: AssetScreenModel,
    onAddClick: () -> Unit,
    accounts: List<AccountData>,
    cashes: List<GetCashData>,
    investments: List<GetInvestmentData>,
    insurances: List<GetInsuranceData>,
    buildings: List<GetBuildingData>,
    lands: List<GetLandData>,
    navigatorContent: Navigator,
) {
    val uiState by screenModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var selectedAssetId by remember { mutableStateOf<String?>(null) }
    var selectedAssetType by remember { mutableStateOf<String?>(null) }
    var showConfirmDelete by remember { mutableStateOf(false) }
    var itemNameToDelete by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        FinancialListTemplate(
            headerTitle = "ทรัพย์สิน & ประกัน",
            themeColor = LightAsset,
            searchQuery = searchQuery,
            onSearchChange = { searchQuery = it },
            onAddClick = onAddClick,
            headerIcon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_asset),
                    contentDescription = null,
                    tint = LightAsset,
                    modifier = Modifier.padding(horizontal = 4.dp).size(28.dp),
                )
            },
        ) {
            AssetListContent(
                searchQuery = searchQuery,
                isLoading = uiState.isLoading,
                error = uiState.error,
                accounts = accounts,
                cashes = cashes,
                investments = investments,
                insurances = insurances,
                buildings = buildings,
                lands = lands,
                onAddClick = onAddClick,
                onRetry = { screenModel.onAction(AssetUiAction.Refresh) },
                onClearSearch = { searchQuery = "" },
                onAssetSelected = { id, type ->
                    selectedAssetId = id
                    selectedAssetType = type
                },
            )
        }

        Surface(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 40.dp, end = 25.dp)
                .size(50.dp)
                .clickable(onClick = onAddClick),
            shape = CircleShape,
            color = LightAsset,
            shadowElevation = 3.dp,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(Res.drawable.ic_common_plus),
                    contentDescription = "เพิ่มทรัพย์สิน",
                    tint = LightSoftWhite,
                    modifier = Modifier.size(40.dp),
                )
            }
        }
    }

    if (showConfirmDelete) {
        val annotatedMessage = buildAnnotatedString {
            append("คุณแน่ใจหรือไม่ว่าต้องการลบ ")
            withStyle(style = SpanStyle(color = LightAsset, fontWeight = FontWeight.Bold)) {
                append("'$itemNameToDelete'")
            }
            append(" ออกจากระบบ?")
        }

        ConfirmDeleteDialog(
            title = "ลบทรัพย์สิน",
            message = annotatedMessage,
            onConfirm = {
                selectedAssetId?.let { id ->
                    selectedAssetType?.let { type -> screenModel.deleteAsset(id, type) }
                }
                showConfirmDelete = false
                selectedAssetId = null
                selectedAssetType = null
            },
            onDismiss = { showConfirmDelete = false },
        )
    }

    if (selectedAssetId != null && selectedAssetType != null && !showConfirmDelete) {
        SmartAssetDetailDialog(
            assetId = requireNotNull(selectedAssetId),
            assetType = requireNotNull(selectedAssetType),
            showBottomMenu = true,
            onDismiss = {
                selectedAssetId = null
                selectedAssetType = null
            },
            onDelete = { itemName ->
                itemNameToDelete = itemName
                showConfirmDelete = true
            },
            onShare = {
                navigatorContent.push(
                    ShareAssetScreen(
                        requireNotNull(selectedAssetType),
                        requireNotNull(selectedAssetId),
                    ),
                )
            },
            onEdit = { rawData -> navigatorContent.openAssetEdit(rawData) },
        )
    }
}
