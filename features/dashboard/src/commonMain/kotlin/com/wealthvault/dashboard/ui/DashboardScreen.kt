package com.wealthvault.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.core.registry.rememberScreen
import com.wealthvault.core.architecture.AppError
import com.wealthvault.core.architecture.CacheFreshness
import com.wealthvault.core.model.DashboardData
import com.wealthvault.core.model.Money
import com.wealthvault.core.navigation.SharedScreen
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

enum class DashboardTab {
    ASSET,
    DEBT,
}

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<DashboardScreenModel>()
        val uiState by screenModel.uiState.collectAsStateWithLifecycle()
        val navigator = LocalNavigator.currentOrThrow
        val localRootNavigator = LocalRootNavigator.current
        val notificationScreen = rememberScreen(SharedScreen.Notification)
        val financialMenuScreen = rememberScreen(SharedScreen.FinancialMenu)

        var rootNavigator = navigator
        while (true) {
            val parentNavigator = rootNavigator.parent ?: break
            rootNavigator = parentNavigator
        }
        var selectedTab by remember { mutableStateOf(DashboardTab.ASSET) }

        DashboardContent(
            onNotiClick = { localRootNavigator.push(notificationScreen) },
            onAddClick = { rootNavigator.push(financialMenuScreen) },
            dashboardState = uiState.data,
            isLoading = uiState.isLoading,
            selectedTab = selectedTab,
            onTabChange = { selectedTab = it },
            hasUnreadNoti = uiState.hasUnreadNotifications,
            freshness = uiState.freshness,
            errorMessage = uiState.error?.let(::dashboardErrorMessage),
            onRetry = { screenModel.onAction(DashboardUiAction.Refresh) },
        )
    }
}

@Composable
fun DashboardContent(
    onNotiClick: () -> Unit,
    onAddClick: () -> Unit,
    dashboardState: DashboardData?,
    isLoading: Boolean,
    selectedTab: DashboardTab,
    onTabChange: (DashboardTab) -> Unit,
    hasUnreadNoti: Boolean,
    freshness: CacheFreshness = CacheFreshness.Fresh,
    errorMessage: String? = null,
    onRetry: () -> Unit = {},
) {
    val listRows = remember(dashboardState, selectedTab) {
        buildDashboardListRows(dashboardState, selectedTab)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp),
    ) {
        DashboardTopBar(onNotiClick = onNotiClick, hasUnreadNoti = hasUnreadNoti)

        if (freshness != CacheFreshness.Fresh) {
            CacheStatusBanner(freshness = freshness, isRefreshing = isLoading)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (isLoading && dashboardState == null) {
            DashboardSkeletonGridCards()
            Spacer(modifier = Modifier.height(40.dp))
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = Color(0xFFC27A5A))
            }
        } else if (dashboardState != null) {
            val data = dashboardState
            DashboardGridCards(
                assetsValue = data.netWorth?.totalAssets ?: Money(0),
                debtsValue = data.netWorth?.totalLiabilities ?: Money(0),
                friendCount = data.friendCount.toString(),
                sharedCount = data.uniqueSharedItemCount.toString(),
                assetCount = data.netWorth?.count.toString(),
                onAssetClick = { onTabChange(DashboardTab.ASSET) },
                onDebtClick = { onTabChange(DashboardTab.DEBT) },
                onAddClick = onAddClick,
                selectedTab = selectedTab,
            )

            Spacer(modifier = Modifier.height(20.dp))

            val isAsset = selectedTab == DashboardTab.ASSET
            val headerTitle = if (isAsset) "มูลค่าทรัพย์สิน" else "มูลค่าหนี้สิน"
            val headerAmountValue = if (isAsset) {
                data.netWorth?.totalAssets ?: Money(0)
            } else {
                data.netWorth?.totalLiabilities ?: Money(0)
            }
            val headerAmount = "${formatAmount(headerAmountValue)} บาท"
            val headerColor = if (isAsset) Color(0xFF398A1E) else Color(0xFFDC4A3C)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(text = headerTitle, style = MaterialTheme.typography.titleSmall, color = Color(0xFFC27A5A))
                Text(text = "≈$headerAmount", style = MaterialTheme.typography.titleSmall, color = headerColor)
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
            ) {
                items(items = listRows, key = DashboardListRow::id) { row ->
                    RealItemCard(
                        title = row.title,
                        subtitleLabel = "ประเภท",
                        subtitleValue = row.categoryName,
                        amountLabel = row.amountLabel,
                        amountValue = row.amountValue,
                    )
                }
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        } else {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = errorMessage ?: "ยังโหลดข้อมูลไม่ได้",
                        color = Color(0xFF7B5E57),
                        textAlign = TextAlign.Center,
                    )
                    Button(
                        onClick = onRetry,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC27A5A)),
                    ) {
                        Text("ลองใหม่", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun CacheStatusBanner(
    freshness: CacheFreshness,
    isRefreshing: Boolean,
) {
    val message = when {
        isRefreshing -> "กำลังอัปเดตข้อมูลล่าสุด…"
        freshness == CacheFreshness.Offline -> "กำลังแสดงข้อมูลที่บันทึกไว้ ออฟไลน์อยู่"
        else -> "กำลังแสดงข้อมูลที่อาจเก่า แตะลองใหม่เพื่ออัปเดต"
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF1E8), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Text(
            text = message,
            color = Color(0xFF7B5E57),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private fun dashboardErrorMessage(error: AppError): String = when (error) {
    AppError.Unauthorized -> "เซสชันหมดอายุ กรุณาเข้าสู่ระบบใหม่"
    AppError.NotFound -> "ไม่พบข้อมูลแดชบอร์ด"
    is AppError.Network -> "เชื่อมต่อเซิร์ฟเวอร์ไม่ได้ กรุณาลองใหม่"
    is AppError.Unknown -> "โหลดข้อมูลไม่สำเร็จ กรุณาลองใหม่"
}
