package com.wealthvault.dashboard.ui

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_common_down_line
import com.wealthvault.core.generated.resources.ic_dashboard_money_bag
import com.wealthvault.core.generated.resources.ic_dashboard_noti
import com.wealthvault.core.generated.resources.ic_dashboard_share
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.generated.resources.ic_nav_debt
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.LocalRootNavigator
import com.wealthvault.core.utils.formatAmount
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault.notification.ui.NotificationScreen
import com.wealthvault.`user-api`.model.DashboardDataResponse
import com.wealthvault_final.`financial-asset`.ui.menu.MenuScreen
import org.jetbrains.compose.resources.painterResource
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.ui.composed
enum class DashboardTab {
    ASSET, DEBT
}

class DashboardScreen(
) : Screen {

    @Composable
    override fun Content() {
        val screenModel = getScreenModel<DashboardScreenModel>()
        val dashboardState by screenModel.dashboardState.collectAsStateWithLifecycle()
        val isLoading by screenModel.isLoading.collectAsStateWithLifecycle()
        val hasUnreadNoti by screenModel.hasUnreadNoti.collectAsStateWithLifecycle()

        val navigator = LocalNavigator.currentOrThrow
        val localRootNavigator = LocalRootNavigator.current

        var rootNavigator = navigator
        while (rootNavigator.parent != null) {
            rootNavigator = rootNavigator.parent!!
        }
        var selectedTab by remember { mutableStateOf(DashboardTab.ASSET) }

        // 🌟 1. ดึง Lifecycle ของหน้าจอมา
        val lifecycleOwner = LocalLifecycleOwner.current

        // 🌟 2. ใช้ DisposableEffect ดัก ON_RESUME เพื่อให้ครอบคลุมทั้ง
        // - การกดสลับหน้าจอกลับมา (Screen lifecycle ขยับเป็น Resume)
        // - การพับแอปแล้วเปิดใหม่ (App lifecycle ขยับเป็น Resume)
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    println("🔄 DashboardScreen กลับมาทำงาน! สั่ง fetchDashboard()...")
                    screenModel.fetchDashboard()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        DashboardContent(
            onNotiClick = {
                localRootNavigator.push(NotificationScreen())
            },
            onAddClick = {
                rootNavigator.push(MenuScreen())
            },
            dashboardState = dashboardState,
            isLoading = isLoading,
            selectedTab = selectedTab,
            onTabChange = { selectedTab = it },
            hasUnreadNoti = hasUnreadNoti
        )
    }
}

@Composable
fun DashboardContent(
    onNotiClick: () -> Unit,
    onAddClick: () -> Unit,
    dashboardState: DashboardDataResponse?,
    isLoading: Boolean,
    selectedTab: DashboardTab,
    onTabChange: (DashboardTab) -> Unit,
    hasUnreadNoti: Boolean // 🌟 รับค่าที่ตรงนี้
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {
        DashboardTopBar(onNotiClick = onNotiClick, hasUnreadNoti = hasUnreadNoti)

        Spacer(modifier = Modifier.height(20.dp))

        // 🌟 แยกส่วน Loading ใหม่ตามสั่ง!
        if (isLoading && dashboardState == null) {

            // 1. ส่วนการ์ดด้านบน: โชว์เป็น Skeleton โครงร่าง
            DashboardSkeletonGridCards()

            Spacer(modifier = Modifier.height(40.dp)) // เว้นระยะลงมาหน่อย

            // 2. ส่วนด้านล่าง: โชว์วงกลมหมุนๆ ตามเดิม
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFC27A5A))
            }

        } else {
            dashboardState?.let { data ->
                DashboardGridCards(
                    assetsValue = data.netWorth?.totalAssets ?: 0.0,
                    debtsValue = data.netWorth?.totalLiabilities ?: 0.0,
                    friendCount = data.friendCount.toString(),
                    sharedCount = data.uniqueSharedItemCount.toString(),
                    assetCount = data.netWorth?.count.toString(),
                    onAssetClick = { onTabChange(DashboardTab.ASSET) },
                    onDebtClick = { onTabChange(DashboardTab.DEBT) },
                    onAddClick = onAddClick,
                    selectedTab = selectedTab
                )

                Spacer(modifier = Modifier.height(20.dp))

                val isAsset = selectedTab == DashboardTab.ASSET
                val headerTitle = if (isAsset) "มูลค่าทรัพย์สิน" else "มูลค่าหนี้สิน"
                val headerAmountValue = if (isAsset) (data.netWorth?.totalAssets ?: 0.0) else (data.netWorth?.totalLiabilities ?: 0.0)
                val headerAmount = formatAmount(headerAmountValue) + " บาท"
                val headerColor = if (isAsset) Color(0xFF398A1E) else Color(0xFFDC4A3C)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = headerTitle, style = MaterialTheme.typography.titleSmall, color = Color(0xFFC27A5A))
                    Text(text = "≈"+headerAmount, style = MaterialTheme.typography.titleSmall, color = headerColor)
                }

                Spacer(modifier = Modifier.height(14.dp))

                val currentList = if (isAsset) data.assets else data.liabilities

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(currentList) { assetItem ->
                        val categoryName = getCategoryGroupName(assetItem.type, isAsset)
                        val amtLabel = when (categoryName) {
                            "บัญชีเงินฝาก" -> "ยอดเงิน"
                            "เงินสด ทองคำ" -> "มูลค่า"
                            "ลงทุน หุ้น กองทุน" -> "มูลค่ารวม"
                            "ประกัน" -> "วงเงินคุ้มครอง"
                            "บ้าน ตึก อาคาร", "ที่ดิน" -> "มูลค่าประเมิน"
                            "หนี้สิน", "รายจ่ายระยะยาว" -> "ยอดหนี้"
                            else -> "มูลค่า"
                        }

                        RealItemCard(
                            title = assetItem.name.ifEmpty { "ไม่ระบุชื่อ" },
                            subtitleLabel = "ประเภท",
                            subtitleValue = categoryName,
                            amountLabel = amtLabel,
                            amountValue = "${assetItem.value?.let { formatAmount(it) } ?: "0"} บาท"
                        )
                    }
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

// =====================================
// 🌟 ฟังก์ชันจัดกลุ่ม (Grouping Helper)
// =====================================
fun getCategoryGroupName(type: String, isAsset: Boolean): String {
    val t = type.lowercase()
    return if (isAsset) {
        when {
            t.contains("account") -> "บัญชีเงินฝาก"
            t.contains("cash") -> "เงินสด ทองคำ"
            t.contains("investment") -> "ลงทุน หุ้น กองทุน"
            t.contains("insurance") -> "ประกัน"
            t.contains("building") -> "บ้าน ตึก อาคาร"
            t.contains("land") -> "ที่ดิน"
            else -> "ทรัพย์สินอื่นๆ"
        }
    } else {
        when {
            t.contains("liability") || t.contains("loan") || t.contains("expense") -> "หนี้สิน"
            else -> "หนี้สินอื่นๆ"
        }
    }
}

@Composable
fun DashboardTopBar(
    onNotiClick: () -> Unit,
    hasUnreadNoti: Boolean = false // 🌟 รับสถานะจุดแดงมา
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Wealth & Vault", style = MaterialTheme.typography.titleLarge, color = Color(0xFFC27A5A), fontWeight = FontWeight.Medium)
        }

        // 🌟 5. ครอบด้วย Box เพื่อแปะจุดแดงทับไอคอน
        Box(
            modifier = Modifier.clickable { onNotiClick() },
            contentAlignment = Alignment.TopEnd
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_dashboard_noti),
                contentDescription = "Notifications",
                tint = Color(0xFFC47B5D),
                modifier = Modifier.size(32.dp).padding(4.dp)
            )

            // 🌟 วาดจุดแดงเมื่อมีข้อความที่ยังไม่อ่าน
            if (hasUnreadNoti) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDC4A3C)) // จุดสีแดง
                        .border(1.5.dp, LightBg, CircleShape) // ใส่ขอบสีพื้นหลังเพื่อให้ดูตัดกันสวยๆ
                )
            }
        }
    }
}

@Composable
fun DashboardGridCards(
    assetsValue: Double,
    debtsValue: Double,
    friendCount: String,
    sharedCount: String,
    assetCount: String,
    onAssetClick: () -> Unit,
    onDebtClick: () -> Unit,
    onAddClick: () -> Unit,
    selectedTab: DashboardTab
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainCard(
                modifier = Modifier.weight(1f),
                bgBrush = Brush.linearGradient(colors = listOf(Color(0xFF6BC591), Color(0xFF26A65B))),
                icon = painterResource(Res.drawable.ic_dashboard_money_bag),
                title = "≈"+formatAmount(assetsValue),
                subtitle = "มูลค่าทรัพย์สิน",
                isSelected = selectedTab == DashboardTab.ASSET,
                onClick = onAssetClick
            )
            SafeCard(modifier = Modifier.weight(1f), count = assetCount, onAddClick = onAddClick)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainCard(
                modifier = Modifier.weight(1f),
                bgBrush = Brush.linearGradient(colors = listOf(Color(0xFFD15E51), Color(0xFFC63A2C))),
                icon = painterResource(Res.drawable.ic_nav_debt),
                title = "≈"+formatAmount(debtsValue),
                subtitle = "มูลค่าหนี้สิน",
                isSelected = selectedTab == DashboardTab.DEBT,
                onClick = onDebtClick
            )
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(colors = listOf(Color(0xFFF9BDA6), Color(0xFFF6A88A))),
                    icon = painterResource(Res.drawable.ic_nav_social),
                    count = friendCount,
                    label = "เพื่อน"
                )
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(colors = listOf(Color(0xFF8BAAFB), Color(0xFF7195F9))),
                    icon = painterResource(Res.drawable.ic_dashboard_share),
                    count = sharedCount,
                    label = "แชร์",
                    label2 = "(ทรัพย์สิน)"
                )
            }
        }
    }
}

@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    bgBrush: Brush,
    icon: Painter? = null,
    title: String,
    subtitle: String,
    isSelected: Boolean = false,
    onClick: () -> Unit = {}
) {
    val cardShape = RoundedCornerShape(14.dp)
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
    val elevation by animateDpAsState(if (isSelected) 12.dp else 2.dp)

    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(elevation = elevation, shape = cardShape)
            .clip(cardShape)
            .background(bgBrush)
            .clickable { onClick() }
            .then(if (isSelected) Modifier.border(2.dp, Color.White.copy(alpha = 0.5f), cardShape) else Modifier)
            .height(80.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            if (icon != null) {
                Icon(painter = icon, contentDescription = null, tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f), modifier = Modifier.size(30.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    color = Color.White,
                    style = when {
                        title.length <= 8 -> MaterialTheme.typography.titleMedium
                        title.length <= 11 -> MaterialTheme.typography.bodyLarge
                        else -> MaterialTheme.typography.bodySmall
                    },
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.9f))
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(painter = painterResource(Res.drawable.ic_common_down_line), contentDescription = null, tint = Color.White.copy(alpha = 0.8f), modifier = Modifier.size(14.dp))
                }
            }
        }
        if (isSelected) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp).size(width = 20.dp, height = 3.dp).background(Color.White, RoundedCornerShape(50)))
        }
    }
}

@Composable
fun SafeCard(modifier: Modifier = Modifier, count: String = "0", onAddClick: () -> Unit = {}) {
    Box(
        modifier = modifier.height(80.dp).shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp)).background(Brush.linearGradient(colors = listOf(Color(0xFFFDAE36), Color(0xFFF3A227)))).padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically){
            Icon(painter = painterResource(Res.drawable.ic_nav_asset), contentDescription = null, tint = Color.White, modifier = Modifier.padding(horizontal = 4.dp).size(24.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = if (count != "null"){count}else "0", color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ทรัพย์สิน", color = Color.White, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 3.dp))
                }
                Spacer(modifier = Modifier.height(2.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .clickable { onAddClick() }
                        .padding(vertical = 4.dp)
                        .height(25.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("เพิ่ม", color = Color(0xFFF1A837), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun SmallCard(modifier: Modifier = Modifier, bgBrush: Brush, icon: Painter? = null, count: String, label: String, label2: String = "") {
    Column(
        modifier = modifier.height(80.dp).shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp)).background(bgBrush).padding(horizontal = 12.dp, vertical = 6.dp).padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) Icon(painter = icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(horizontal = 4.dp).size(22.dp))
            val countStyle = if (count.length > 2) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium
            Text(text = count, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center, color = Color.White, style = countStyle, fontWeight = FontWeight.Bold)
        }
        Column (modifier = Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, color = Color.White, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
            if (label2.isNotEmpty()) {
                Text(text = label2, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center, modifier = Modifier.offset(y = (-6).dp))
            }
        }
    }
}

@Composable
fun RealItemCard(
    title: String,
    subtitleLabel: String,
    subtitleValue: String,
    amountLabel: String,
    amountValue: String,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = LightSoftWhite
        ),
        border = BorderStroke(1.dp, LightBorder),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF3A2F2A))
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = subtitleLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = subtitleValue, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = amountLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = amountValue, fontSize = 14.sp, color = Color(0xFF3A2F2A))
            }
        }
    }
}

// ==========================================
// 🌟 Skeleton Loading Components
// ==========================================

@Composable
fun DashboardSkeletonGridCards() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // แถวแรก: MainCard(ซ้าย) + SafeCard(ขวา)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
        }

        // แถวสอง: MainCard(ซ้าย) + Row ของ SmallCard 2 อัน(ขวา)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
                Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
            }
        }
    }
}

// Modifier สำหรับทำแอนิเมชันสีเทาวูบวาบ (Shimmer Pulse)
fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )
    this.background(Color(0xFFE0E0E0).copy(alpha = alpha))
}
