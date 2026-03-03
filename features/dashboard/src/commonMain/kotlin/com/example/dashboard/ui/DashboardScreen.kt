package com.wealthvault.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
fun DashboardScreen() {
    // State ควบคุมการแสดงผลข้อมูลด้านล่าง (เริ่มต้นที่ ทรัพย์สิน)
    var selectedTab by remember { mutableStateOf(DashboardTab.ASSET) }

    // ข้อมูลจำลอง (Mock Data)
    val mockAssets = listOf(
        FinancialItem("บัญชีให้ลูก", "ธนาคาร", "SCB", "มูลค่าทรัพย์สิน", "30,000 บาท"),
        FinancialItem("กองทุนรวม", "บลจ.", "K-Asset", "มูลค่าทรัพย์สิน", "150,000 บาท"),
        FinancialItem("เงินสด", "ประเภท", "กระเป๋าตังค์", "มูลค่าทรัพย์สิน", "20,000 บาท")
    )

    val mockDebts = listOf(
        FinancialItem("ผ่อนบ้าน", "ธนาคาร", "KBANK", "มูลค่าหนี้สิน", "1,500,000 บาท"),
        FinancialItem("บัตรเครดิต", "บัตร", "KTC", "มูลค่าหนี้สิน", "15,000 บาท")
    )

    // โครงสร้างหลัก (ใช้ Scaffold เพื่อรองรับสีพื้นหลัง แต่ไม่มี bottomBar แล้ว)
    Scaffold(
        containerColor = Color(0xFFFFF8F3) // สีพื้นหลังหลัก
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // 1. Top Bar (Logo + Notification)
            DashboardTopBar()

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Grid Cards (4 กล่องด้านบน)
            DashboardGridCards(
                onAssetClick = { selectedTab = DashboardTab.ASSET },
                onDebtClick = { selectedTab = DashboardTab.DEBT }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Header ของรายการ (เปลี่ยนสีตาม Tab)
            val isAsset = selectedTab == DashboardTab.ASSET
            val headerTitle = if (isAsset) "มูลค่าทรัพย์สิน≈" else "มูลค่าหนี้สิน≈"
            val headerAmount = if (isAsset) "200,000 บาท" else "30,000 บาท"
            val headerColor = if (isAsset) Color(0xFF4CB37E) else Color(0xFFD3554A)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = headerTitle, fontSize = 20.sp, color = headerColor, fontWeight = FontWeight.SemiBold)
                Text(text = headerAmount, fontSize = 20.sp, color = headerColor, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. รายการข้อมูล (LazyColumn สกอร์ลเลื่อนได้)
            val currentList = if (isAsset) mockAssets else mockDebts
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentList) { item ->
                    FinancialItemCard(item)
                }
            }
        }
    }
}

@Composable
fun DashboardTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // TODO: แทนที่ด้วย Icon หรือ Image Logo ของจริง
            Text("W", fontSize = 32.sp, color = Color(0xFFC47B5D), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Wealth & Vault", fontSize = 24.sp, color = Color(0xFFC47B5D), fontWeight = FontWeight.Medium)
        }
//        Icon(
//            imageVector = Icons.Default.Notifications,
//            contentDescription = "Notifications",
//            tint = Color(0xFFC47B5D),
//            modifier = Modifier.size(28.dp)
//        )
    }
}

@Composable
fun DashboardGridCards(onAssetClick: () -> Unit, onDebtClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // แถวที่ 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // กล่องเขียว (ทรัพย์สิน) -> กดแล้วแสดงข้อมูลทรัพย์สิน
            MainCard(
                modifier = Modifier.weight(1f).clickable { onAssetClick() },
                bgColor = Color(0xFF4CB37E),
//                icon = Icons.Default.AttachMoney,
                title = "200,000",
                subtitle = "มูลค่าทรัพย์สิน≈"
            )
            // กล่องเหลือง (ตู้เซฟ)
            SafeCard(modifier = Modifier.weight(1f))
        }

        // แถวที่ 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // กล่องแดง (หนี้สิน) -> กดแล้วแสดงข้อมูลหนี้สิน
            MainCard(
                modifier = Modifier.weight(1f).clickable { onDebtClick() },
                bgColor = Color(0xFFD3554A),
//                icon = Icons.Default.AccountBalance,
                title = "30,000",
                subtitle = "มูลค่าหนี้สิน≈"
            )
            // กล่องเล็ก 2 กล่อง (เพื่อน, แชร์)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SmallCard(modifier = Modifier.weight(1f), bgColor = Color(0xFFF3AD8E),
//                    icon = Icons.Default.Group,
                    count = "6", label = "เพื่อน")
                SmallCard(modifier = Modifier.weight(1f), bgColor = Color(0xFF7E9DF9),
//                    icon = Icons.Default.Share,
                    count = "6", label = "แชร์\n(ทรัพย์สิน)")
            }
        }
    }
}

// คอมโพเนนต์ย่อยสำหรับวาดกล่องสีต่างๆ
@Composable
fun MainCard(modifier: Modifier = Modifier, bgColor: Color,
//             icon: ImageVector,
             title: String, subtitle: String) {
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
//            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SafeCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF1A837))
            .padding(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "7", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ทรัพย์สิน", color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(bottom = 3.dp))
                }
                Spacer(modifier = Modifier.height(4.dp))
                // ปุ่ม "เพิ่ม" สีขาว
                Box(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color.White).padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("เพิ่ม", color = Color(0xFFF1A837), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SmallCard(modifier: Modifier = Modifier, bgColor: Color,
//              icon: ImageVector,
              count: String, label: String) {
    Column(
        modifier = modifier
            .height(90.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = count, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = label, color = Color.White, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 12.sp)
    }
}

@Composable
fun FinancialItemCard(item: FinancialItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = item.title, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.typeLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = item.typeValue, fontSize = 14.sp, color = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.valueLabel, fontSize = 14.sp, color = Color.Gray)
                Text(text = item.amountStr, fontSize = 14.sp, color = Color.Black)
            }
        }
    }
}