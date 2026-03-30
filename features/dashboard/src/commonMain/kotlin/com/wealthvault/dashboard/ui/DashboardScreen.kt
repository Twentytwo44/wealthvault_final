package com.wealthvault.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.wealthvault.core.theme.LightSoftWhite
import org.jetbrains.compose.resources.painterResource

// --- ตัวแปรสำหรับสลับหน้า (State) ---
enum class DashboardTab {
    ASSET, DEBT
}

// --- Mock Data Class ---
data class FinancialItem(
    val title: String,
    val typeLabel: String,
    val typeValue: String,
    val valueLabel: String,
    val amountStr: String
)

@Composable
fun DashboardScreen(onNotiClick: () -> Unit = {}) {
    // State ควบคุมการแสดงผลข้อมูลด้านล่าง
    var selectedTab by remember { mutableStateOf(DashboardTab.ASSET) }

    // ข้อมูลจำลอง (Mock Data)
    val mockAssets = listOf(
        FinancialItem("บัญชีให้ลูก", "ธนาคาร", "SCB", "มูลค่าทรัพย์สิน", "30,000 บาท"),
        FinancialItem("กองทุนรวม", "บลจ.", "K-Asset", "มูลค่าทรัพย์สิน", "150,000 บาท"),
        FinancialItem("เงินสด", "ประเภท", "กระเป๋าตังค์", "มูลค่าทรัพย์สิน", "20,000 บาท"),
        FinancialItem("กองทุนรวม", "บลจ.", "K-Asset", "มูลค่าทรัพย์สิน", "150,000 บาท"),
        FinancialItem("เงินสด", "ประเภท", "กระเป๋าตังค์", "มูลค่าทรัพย์สิน", "20,000 บาท")
    )

    val mockDebts = listOf(
        FinancialItem("ผ่อนบ้าน", "ธนาคาร", "KBANK", "มูลค่าหนี้สิน", "1,500,000 บาท"),
        FinancialItem("บัตรเครดิต", "บัตร", "KTC", "มูลค่าหนี้สิน", "15,000 บาท")
    )

    // 🌟 1. ถอด Scaffold ออก ใช้แค่ Column เป็นโครงสร้างหลัก 🌟
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .padding(top = 20.dp)
    ) {

        // 1. Top Bar (Logo + Notification)
        DashboardTopBar(onNotiClick = onNotiClick)

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Grid Cards (4 กล่องด้านบน)
        DashboardGridCards(
            onAssetClick = { selectedTab = DashboardTab.ASSET },
            onDebtClick = { selectedTab = DashboardTab.DEBT },
            selectedTab
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Header ของรายการ (เปลี่ยนสีตาม Tab)
        val isAsset = selectedTab == DashboardTab.ASSET
        val headerTitle = if (isAsset) "มูลค่าทรัพย์สิน≈" else "มูลค่าหนี้สิน≈"
        val headerAmount = if (isAsset) "200,000 บาท" else "30,000 บาท"
        val headerColor = if (isAsset) Color(0xFF398A1E) else Color(0xFFDC4A3C)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = headerTitle,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFC27A5A))
            Text(text = headerAmount,
                style = MaterialTheme.typography.titleMedium,
                color = headerColor)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 4. รายการข้อมูล (LazyColumn)
        val currentList = if (isAsset) mockAssets else mockDebts
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
            // 🌟 3. เพิ่มระยะหายใจให้ด้านล่างสุด (ลากรายการสุดท้ายขึ้นมาเหนือ Navbar ได้สวยๆ) 🌟
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(currentList) { item ->
                FinancialItemCard(item)
            }
        }
    }
}

@Composable
fun DashboardTopBar(onNotiClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("W", fontSize = 32.sp, color = Color(0xFFC27A5A), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Wealth & Vault",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFFC27A5A),
                fontWeight = FontWeight.Medium
            )
        }
        Icon(
            painter = painterResource(Res.drawable.ic_dashboard_noti),
            contentDescription = "Notifications",
            tint = Color(0xFFC47B5D),
            modifier = Modifier
                .size(32.dp)
                .clickable { onNotiClick() } // 🌟 4. กดปุ๊บ สั่งงานปั๊บ!
        )
    }
}

@Composable
fun DashboardGridCards(
    onAssetClick: () -> Unit,
    onDebtClick: () -> Unit,
    selectedTab: DashboardTab
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // แถวที่ 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MainCard(
                modifier = Modifier.weight(1f).clickable { onAssetClick() },
                bgBrush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6BC591), Color(0xFF26A65B))
                ),
                // 🌟 วิธีใส่ไอคอน: เอาคอมเมนต์ออกแล้วเปลี่ยนชื่อไฟล์ตรงนี้ได้เลย
                 icon = painterResource(Res.drawable.ic_dashboard_money_bag),
                title = "200,000",
                subtitle = "มูลค่าทรัพย์สิน≈",
                isSelected = selectedTab == DashboardTab.ASSET
            )
            SafeCard(modifier = Modifier.weight(1f))
        }

        // แถวที่ 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MainCard(
                modifier = Modifier.weight(1f).clickable { onDebtClick() },
                bgBrush = Brush.linearGradient(
                    colors = listOf(Color(0xFFD15E51), Color(0xFFC63A2C))
                ),
                icon = painterResource(Res.drawable.ic_nav_debt),
                title = "30,000",
                subtitle = "มูลค่าหนี้สิน≈",
                isSelected = selectedTab == DashboardTab.DEBT
            )
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(
                        colors = listOf(Color(0xFFF9BDA6), Color(0xFFF6A88A))
                    ),
                     icon = painterResource(Res.drawable.ic_nav_social),
                    count = "6",
                    label = "เพื่อน"
                )
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(
                        colors = listOf(Color(0xFF8BAAFB), Color(0xFF7195F9))
                    ),
                     icon = painterResource(Res.drawable.ic_dashboard_share),
                    count = "6",
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
    icon: Painter? = null, // 🌟 เพิ่มช่องรับไอคอน (ตั้งค่าเริ่มต้นเป็น null คือยังไม่ใส่ก็ได้)
    title: String,
    subtitle: String,
    isSelected: Boolean = false
) {
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(90.dp)
            .shadow(elevation = if (isSelected) 8.dp else 4.dp, shape = cardShape)
            .then(
                if (isSelected) Modifier.border(width = 4.dp, color = Color.White, shape = cardShape)
                else Modifier
            )
            .background(bgBrush, cardShape)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 🌟 เช็คว่าถ้ามีการส่งไอคอนมา ถึงจะวาดไอคอนและเว้นระยะให้
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp).size(38.dp)
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),)
            }

        }
    }
}

@Composable
fun SafeCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(90.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFFDAE36), Color(0xFFF3A227))
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        // 🌟 ถอด Row ที่เคยครอบ Spacer ล่องหนออก แล้วจัดเรียงใหม่ให้สวยงาม
        Row(verticalAlignment = Alignment.CenterVertically){
            if (painterResource(Res.drawable.ic_nav_asset) != null) {
                Icon(
                    painter = painterResource(Res.drawable.ic_nav_asset),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp).size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.fillMaxWidth()
        ) {

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "7",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "ทรัพย์สิน",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            // ปุ่ม "เพิ่ม" สีขาว
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .padding(vertical = 4.dp)
                    .height(25.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("เพิ่ม",
                    color = Color(0xFFF1A837),
                    style = MaterialTheme.typography.bodyMedium,)
            }
        }}
    }
}


@Composable
fun SmallCard(
    modifier: Modifier = Modifier,
    bgBrush: Brush,
    icon: Painter? = null,
    count: String,
    label: String,
    label2: String = ""
) {
    Column(
        modifier = modifier // 🌟 กล่องนอกสุดใช้ modifier (m เล็ก) ได้ที่เดียว!
            .height(90.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .background(bgBrush)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .padding(top = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // --- ครึ่งบน ---
        Row(
            modifier = Modifier.fillMaxWidth(), // 🌟 ข้างในต้องเริ่มใหม่ด้วย Modifier (M ใหญ่) 🌟
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(horizontal = 4.dp).size(26.dp) // 🌟 M ใหญ่
                )
            } else {
                Spacer(modifier = Modifier.width(26.dp)) // ใส่ช่องว่างกันไว้เผื่อไม่มีรูป จะได้ไม่เบี้ยว
            }

            Text(
                text = count,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        // --- ครึ่งล่าง ---
        Column (
            modifier = Modifier.fillMaxHeight()
                , // 🌟 M ใหญ่
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, color = Color.White,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center)

            if (label2.isNotEmpty()) {
                Text(
                    text = label2,
                    color = Color.White.copy(alpha = 0.9f),
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.offset(y = (-6).dp)
                )
            }
        }
    }
}

@Composable
fun FinancialItemCard(item: FinancialItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(LightSoftWhite)
            .border(
                width = 1.dp,
                color = Color(0xFFF3E5D8), // สีพาสเทลตามที่คุณกำหนด
                shape = RoundedCornerShape(16.dp) // สำคัญ! ต้องใส่ shape ให้ตรงกับของ Card
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = item.typeLabel, fontSize = 14.sp, color = Color(0xFF3A2F2A))
                Text(text = item.typeValue, fontSize = 14.sp, color = Color(0xFF7A6A62))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = item.valueLabel, fontSize = 14.sp, color = Color(0xFF3A2F2A))
                Text(text = item.amountStr, fontSize = 14.sp, color = Color(0xFF7A6A62))
            }
        }
    }
}