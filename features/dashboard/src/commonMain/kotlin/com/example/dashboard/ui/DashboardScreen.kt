package com.wealthvault.dashboard.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
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
        FinancialItem("เงินสด", "ประเภท", "กระเป๋าตังค์", "มูลค่าทรัพย์สิน", "20,000 บาท"),
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
                Text(text = headerTitle, fontSize = 20.sp, color = Color(0xFFC27A5A))
                Text(text = headerAmount, fontSize = 20.sp, color = headerColor)
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
            Text("W", fontSize = 32.sp, color = Color(0xFFC27A5A), fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Wealth & Vault", fontSize = 24.sp, color = Color(0xFFC27A5A), fontWeight = FontWeight.Medium)
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
fun DashboardGridCards(onAssetClick: () -> Unit, onDebtClick: () -> Unit, selectedTab: DashboardTab) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // แถวที่ 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // กล่องเขียว (ทรัพย์สิน) -> กดแล้วแสดงข้อมูลทรัพย์สิน
            MainCard(
                modifier = Modifier.weight(1f).clickable { onAssetClick() },
                // 👇 ใช้ Brush.linearGradient แทนการกำหนดสีเดียว
                bgBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6BC591), // สีเริ่มต้น (ซ้ายบน)
                        Color(0xFF26A65B)  // สีสิ้นสุด (ขวาล่าง)
                    )
                ),
            //  icon = Icons.Default.AttachMoney,
                title = "200,000",
                subtitle = "มูลค่าทรัพย์สิน≈",
                isSelected = selectedTab == DashboardTab.ASSET
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
                bgBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFD15E51), // สีเริ่มต้น (ซ้ายบน)
                        Color(0xFFC63A2C)  // สีสิ้นสุด (ขวาล่าง)
                    )
                ),
//                icon = Icons.Default.AccountBalance,
                title = "30,000",
                subtitle = "มูลค่าหนี้สิน≈",
                isSelected = selectedTab == DashboardTab.DEBT
            )
            // กล่องเล็ก 2 กล่อง (เพื่อน, แชร์)
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // กล่องส้ม (เพื่อน)
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFF9BDA6), // สีเริ่มต้น (ซ้ายบน)
                            Color(0xFFF6A88A)  // สีสิ้นสุด (ขวาล่าง)
                        )
                    ), // 👈 ลบวงเล็บปิด ) ที่เคยเกินออกตรงนี้ครับ
//  icon = Icons.Default.Group,
                    count = "6",
                    label = "เพื่อน"
                )

// กล่องฟ้า (แชร์)
                SmallCard(
                    modifier = Modifier.weight(1f),
                    bgBrush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8BAAFB),
                            Color(0xFF7195F9)
                        )
                    ),
//  icon = Icons.Default.Share,
                    count = "6",
                    label = "แชร์",
                    label2 = "(ทรัพย์สิน)"
                )
            }
        }
    }
}

// คอมโพเนนต์ย่อยสำหรับวาดกล่องสีต่างๆ
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    bgBrush: Brush,
//  icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean = false // 👈 1. เพิ่ม Parameter รับสถานะการเลือก (Default เป็น false)
) {
    // 💡 2. กำหนด Shape ไว้ที่ตัวแปรเดียวเพื่อใช้ร่วมกันทั้ง shadow, border และ background
    val cardShape = RoundedCornerShape(16.dp)

    Box(
        modifier = modifier
            .height(80.dp)
            // 👇 3. ใส่ shadow เสมอ (ปรับ elevation ตามความเหมาะสม)
            .shadow(
                elevation = if (isSelected) 8.dp else 4.dp, // เลือกแล้วให้ลอยเด่นขึ้นนิดนึง
                shape = cardShape
            )
            // 👇 4. ใช้ Modifier.then เพื่อใส่ border ตามเงื่อนไข isSelected
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp, // ความหนาของขอบ
                        color = Color.White, // สีของขอบ (หรือสี Theme Primary)
                        shape = cardShape
                    )
                } else {
                    Modifier // ถ้าไม่เลือก ไม่ต้องใส่ border
                }
            )
            .background(bgBrush, cardShape) // ใส่ shape ให้ background ด้วยเพื่อความเป๊ะ
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
//            Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(text = title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.9f), fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun SafeCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(80.dp)
            .shadow(
                elevation = 4.dp, // ปรับตัวเลขตรงนี้เพื่อเพิ่ม/ลดความฟุ้งของเงา (เช่น 4.dp, 8.dp)
                shape = RoundedCornerShape(16.dp)
            )
            .background(Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFDAE36), // สีเริ่มต้น (ซ้ายบน)
                    Color(0xFFF3A227)  // สีสิ้นสุด (ขวาล่าง)
                )
            ))
            .padding(10.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
//            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(text = "7", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "ทรัพย์สิน", color = Color.White, fontSize = 16.sp, modifier = Modifier.padding(bottom = 3.dp))
                }
                Spacer(modifier = Modifier.height(2.dp))
                // ปุ่ม "เพิ่ม" สีขาว
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White)
                        .padding(vertical = 4.dp)
                        .height(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("เพิ่ม", color = Color(0xFFF1A837), fontSize = 12.sp)
                }
            }
        }
    }
}


@Composable
fun SmallCard(
    modifier: Modifier = Modifier,
    bgBrush: Brush, // 👈 1. เปลี่ยนจาก bgColor: Color เป็น bgBrush: Brush
    count: String,
    label: String,
    label2: String = ""
) {
    Column(
        modifier = modifier
            .height(80.dp)
            .shadow(
                elevation = 4.dp, // ปรับตัวเลขตรงนี้เพื่อเพิ่ม/ลดความฟุ้งของเงา (เช่น 4.dp, 8.dp)
                shape = RoundedCornerShape(16.dp)
            )
            .background(bgBrush), // 👈 2. ระบายสีพื้นหลังด้วย Brush
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ครึ่งบน: พื้นที่สำหรับตัวเลข ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(text = count, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        // --- ครึ่งล่าง: พื้นที่สำหรับข้อความ ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = label, color = Color.White, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                if (label2.isNotEmpty()) {
                    Text(text = label2, color = Color.White, fontSize = 11.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
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
            .border(
                width = 1.dp,
                color = Color(0xFFF3E5D8), // สีพาสเทลตามที่คุณกำหนด
                shape = RoundedCornerShape(16.dp) // สำคัญ! ต้องใส่ shape ให้ตรงกับของ Card
            )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = item.title, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.typeLabel, fontSize = 14.sp, color = Color(0xFF3A2F2A))
                Text(text = item.typeValue, fontSize = 14.sp, color = Color(0xFF7A6A62))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = item.valueLabel, fontSize = 14.sp, color = Color(0xFF3A2F2A))
                Text(text = item.amountStr, fontSize = 14.sp, color = Color(0xFF7A6A62))
            }
        }
    }
}