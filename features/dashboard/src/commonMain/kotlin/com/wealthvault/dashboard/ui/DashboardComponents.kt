package com.wealthvault.dashboard.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.ic_dashboard_money_bag
import com.wealthvault.core.generated.resources.ic_dashboard_noti
import com.wealthvault.core.generated.resources.ic_dashboard_share
import com.wealthvault.core.generated.resources.ic_nav_asset
import com.wealthvault.core.generated.resources.ic_nav_debt
import com.wealthvault.core.generated.resources.ic_nav_social
import com.wealthvault.core.model.Money
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightBorder
import com.wealthvault.core.theme.LightSoftWhite
import com.wealthvault.core.utils.formatAmount
import org.jetbrains.compose.resources.painterResource

@Composable
fun DashboardTopBar(
    onNotiClick: () -> Unit,
    hasUnreadNoti: Boolean = false,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "Wealth & Vault",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFC27A5A),
                fontWeight = FontWeight.Medium,
            )
        }

        Box(
            modifier = Modifier.clickable { onNotiClick() },
            contentAlignment = Alignment.TopEnd,
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_dashboard_noti),
                contentDescription = "Notifications",
                tint = Color(0xFFC47B5D),
                modifier = Modifier.size(32.dp).padding(4.dp),
            )
            if (hasUnreadNoti) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDC4A3C))
                        .border(1.5.dp, LightBg, CircleShape),
                )
            }
        }
    }
}

@Composable
fun DashboardGridCards(
    assetsValue: Money,
    debtsValue: Money,
    friendCount: String,
    sharedCount: String,
    assetCount: String,
    onAssetClick: () -> Unit,
    onDebtClick: () -> Unit,
    onAddClick: () -> Unit,
    selectedTab: DashboardTab,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainCard(
                modifier = Modifier.weight(1f),
                bgBrush = Brush.linearGradient(colors = listOf(Color(0xFF6BC591), Color(0xFF26A65B))),
                icon = painterResource(Res.drawable.ic_dashboard_money_bag),
                title = "≈" + formatAmount(assetsValue),
                subtitle = "มูลค่าทรัพย์สิน",
                isSelected = selectedTab == DashboardTab.ASSET,
                onClick = onAssetClick,
            )
            SafeCard(modifier = Modifier.weight(1f), count = assetCount, onAddClick = onAddClick)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            MainCard(
                modifier = Modifier.weight(1f),
                bgBrush = Brush.linearGradient(colors = listOf(Color(0xFFD15E51), Color(0xFFC63A2C))),
                icon = painterResource(Res.drawable.ic_nav_debt),
                title = "≈" + formatAmount(debtsValue),
                subtitle = "มูลค่าหนี้สิน",
                isSelected = selectedTab == DashboardTab.DEBT,
                onClick = onDebtClick,
            )
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(colors = listOf(Color(0xFFF9BDA6), Color(0xFFF6A88A))),
                    icon = painterResource(Res.drawable.ic_nav_social),
                    count = friendCount,
                    label = "เพื่อน",
                )
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(colors = listOf(Color(0xFF8BAAFB), Color(0xFF7195F9))),
                    icon = painterResource(Res.drawable.ic_dashboard_share),
                    count = sharedCount,
                    label = "แชร์",
                    label2 = "(ทรัพย์สิน)",
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
    onClick: () -> Unit = {},
) {
    val cardShape = RoundedCornerShape(14.dp)
    val scale by animateFloatAsState(if (isSelected) 1.05f else 1f)
    val elevation by animateDpAsState(if (isSelected) 12.dp else 2.dp)
    val indicatorWidth by animateDpAsState(
        targetValue = if (isSelected) 20.dp else 14.dp,
        animationSpec = tween(durationMillis = 300),
    )
    val indicatorHeight by animateDpAsState(
        targetValue = if (isSelected) 3.dp else 2.dp,
        animationSpec = tween(durationMillis = 300),
    )
    val indicatorAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.6f,
        animationSpec = tween(durationMillis = 300),
    )
    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
            .shadow(elevation = elevation, shape = cardShape)
            .clip(cardShape)
            .background(bgBrush)
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f),
                shape = cardShape,
            )
            .height(80.dp)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(30.dp),
                )
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
                    maxLines = 1,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.9f),
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
                .size(width = indicatorWidth, height = indicatorHeight)
                .background(
                    color = Color.White.copy(alpha = indicatorAlpha),
                    shape = RoundedCornerShape(50),
                ),
        )
    }
}

@Composable
fun SafeCard(modifier: Modifier = Modifier, count: String = "0", onAddClick: () -> Unit = {}) {
    Box(
        modifier = modifier
            .height(80.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(colors = listOf(Color(0xFFFDAE36), Color(0xFFF3A227))))
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(Res.drawable.ic_nav_asset),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(horizontal = 4.dp).size(24.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = if (count != "null") count else "0",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ทรัพย์สิน",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 3.dp),
                    )
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
                    contentAlignment = Alignment.Center,
                ) {
                    Text("เพิ่ม", color = Color(0xFFF1A837), style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun SmallCard(
    modifier: Modifier = Modifier,
    bgBrush: Brush,
    icon: Painter? = null,
    count: String,
    label: String,
    label2: String = "",
) {
    Column(
        modifier = modifier
            .height(80.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(14.dp))
            .background(bgBrush)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp).size(22.dp),
                )
            }
            val countStyle = if (count.length > 2) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.titleMedium
            Text(
                text = count,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = countStyle,
                fontWeight = FontWeight.Bold,
            )
        }
        Column(
            modifier = Modifier.fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = label, color = Color.White, style = MaterialTheme.typography.labelMedium, textAlign = TextAlign.Center)
            if (label2.isNotEmpty()) {
                Text(
                    text = label2,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-6).dp),
                )
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
        modifier = Modifier.fillMaxWidth().padding(bottom = 2.dp),
        colors = CardDefaults.cardColors(containerColor = LightSoftWhite),
        border = BorderStroke(1.dp, LightBorder),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
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

@Composable
fun DashboardSkeletonGridCards() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
            Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
                Box(modifier = Modifier.weight(1f).height(80.dp).clip(RoundedCornerShape(14.dp)).shimmerEffect())
            }
        }
    }
}

fun Modifier.shimmerEffect(): Modifier = composed {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "shimmer_alpha",
    )
    this.background(Color(0xFFE0E0E0).copy(alpha = alpha))
}
