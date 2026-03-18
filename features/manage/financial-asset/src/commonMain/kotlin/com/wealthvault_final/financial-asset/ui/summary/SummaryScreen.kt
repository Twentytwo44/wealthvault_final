package com.wealthvault_final.`financial-asset`.ui.summary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val WealthVaultBrown = Color(0xFFB37E61)
val WealthVaultBackground = Color(0xFFFFF8F3)
val WealthVaultCardHeader = Color(0xFF6D4C41)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {}
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding() // เว้นแถบแบตเตอรี่/เวลา
            .navigationBarsPadding(), // เว้นแถบ Home ด้านล่าง
        containerColor = Color(0xFFFFF8F3),
        topBar = {
            // ใช้ CenterAlignedTopAppBar ตัวเดียวให้จบ
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                title = {
                    Text(
                        "สรุป", // เปลี่ยนเป็นคำว่าสรุปตามรูป
                        color = WealthVaultBrown,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = null,
                            tint = WealthVaultBrown
                        )
                    }
                }
            )
        },
        bottomBar = {
            // --- ส่วนที่ Fixed ไว้ด้านล่างเสมอ ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Button(
                    onClick = onConfirmClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp), // มนสวยตามสไตล์ปุ่มยืนยัน
                    colors = ButtonDefaults.buttonColors(containerColor = WealthVaultBrown)
                ) {
                    Text("ยืนยัน", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        // --- ส่วนที่เลื่อนได้ (Scrollable Content) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // ใช้ padding จาก Scaffold (กัน TopBar ทับ)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()) // ทำให้เลื่อนดูข้อมูลยาวๆ ได้
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Section 1: ข้อมูลหุ้น กองทุน
            Text(
                "ข้อมูลหุ้น กองทุน",
                color = WealthVaultBrown,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            SummaryCard() // ตัวนี้ที่เราทำ Scroll ภายในไว้ หรือจะให้เลื่อนไปพร้อมหน้าจอก็ได้

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: แชร์
            Text(
                "แชร์",
                color = WealthVaultBrown,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShareSection()

            // เผื่อช่องว่างด้านล่างสุดให้เลื่อนพ้นปุ่ม
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable
fun SummaryCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 400.dp), // กำหนดความสูงสูงสุดเพื่อให้ส่วนอื่นยังมองเห็นได้
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3E9D8))
    ) {
        // ส่วนนี้คือจุดสำคัญที่ทำให้ Scroll ได้
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            SummaryRow("ชื่อหุ้น กองทุน", "S&P500")
            SummaryRow("จำนวนหุ้น กองทุน", "300,000")
            SummaryRow("ราคาที่ซื้อ", "13.43 บาท/หน่วย")
            SummaryRow("คำอธิบาย", "คำอธิบายยาวๆ สามารถใส่เพิ่มตรงนี้ได้เลย ข้อมูลจะเลื่อนขึ้นลงได้ตามต้องการ")

            Spacer(modifier = Modifier.height(16.dp))
            Text("ข้อมูลอ้างอิง", color = WealthVaultBrown, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            // ส่วนรูปภาพอ้างอิง: ใช้ LazyRow เพื่อให้เลื่อนซ้าย-ขวาได้ด้วย
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(5) { // สมมติว่ามี 5 รูป
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color(0xFFFFF8F3), RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFFF3E9D8), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = WealthVaultBrown.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = WealthVaultBrown)
        Text(value, color = Color.Gray)
    }
}
@Composable
fun ShareSection() {
    // กรอบนอกสุดสีฟ้า
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F3)),
        border = BorderStroke(1.dp, Color(0xFFF3E5D8))

    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) // ระยะห่างระหว่าง item
        ) {
            // Item 1: Person
            ShareItemCard(name = "Two")

            // Item 2: Group พร้อม Badge
            ShareItemCard(name = "Group 1", groupCount = "5")

            // เส้นคั่นที่ไม่สุดขอบ
            Divider(
                modifier = Modifier.padding(horizontal = 8.dp),
                color = WealthVaultBrown.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            // Item 3: Email
            ShareItemCard(
                name = "example@gmail.com",
                isEmail = true
            )
        }
    }
}

@Composable
fun ShareItemCard(
    name: String,
    groupCount: String? = null,
    isEmail: Boolean = false
) {
    // แต่ละ Item มีพื้นหลังสีขาวและขอบมน
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ส่วนของ Icon Box
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        color = if (isEmail) Color(0xFFF2E8E1) else Color(0xFFD9D9D9),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isEmail) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = WealthVaultBrown,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // ถ้าไม่ใช่ Email ในรูปจะเป็นพื้นที่สีเทาว่างๆ
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    fontSize = 18.sp,
                    color = Color(0xFF4A4A4A)
                )

                // Badge สำหรับจำนวนคนในกลุ่ม
                if (groupCount != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        color = Color(0xFFF2E8E1),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Groups,
                                contentDescription = null,
                                // ✅ ต้องใช้ Modifier.size และหน่วยต้องเป็น .dp (ไม่ใช่ .sp)
                                modifier = Modifier.size(16.dp),
                                tint = WealthVaultBrown
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = groupCount,
                                fontSize = 12.sp,
                                color = WealthVaultBrown
                            )
                        }
                    }
                }
            }
        }
    }
}
