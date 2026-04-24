package com.wealthvault_final.`financial-obligations`.ui.liability.summary

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.model.ShareTo
import com.wealthvault_final.`financial-asset`.ui.components.ShareItemCard
import com.wealthvault_final.`financial-obligations`.model.LiabilityModel

val WealthVaultBrown = Color(0xFFB37E61)
val WealthVaultBackground = Color(0xFFFFF8F3)
val WealthVaultCardHeader = Color(0xFF6D4C41)


data class LiabilitySummaryScreen(val request: LiabilityModel, val shareTo: ShareTo) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        // ดึง SummaryScreenModel มาตามปกติ
        val screenModel = getScreenModel<LiabilitySummaryScreenModel>()

        // 🚩 สำคัญ: ส่งข้อมูลที่ได้รับจาก Constructor ให้ ScreenModel ทันที
        LaunchedEffect(Unit) {
            screenModel.initData(request)
            screenModel.initShareInfo(shareTo)
        }

        val state by screenModel.state.collectAsState()

        SummaryContent(
            onBackClick = { navigator.pop() },
            onConfirmClick = { screenModel.submitLiability(
                onSuccess = {
                    navigator.popUntilRoot()
                }
            ) }, // ตอนนี้ ScreenModel จะมีข้อมูลพร้อมส่งแล้ว
            data = state.liabilityRequest,
            shareInfo = state.shareTo,

        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryContent(
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    data: LiabilityModel?,
    shareInfo: ShareTo?,
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

            Text(
                "ข้อมูลเงินสด",
                color = WealthVaultBrown,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            SummaryCard(data ?: return@Column) // ตัวนี้ที่เราทำ Scroll ภายในไว้ หรือจะให้เลื่อนไปพร้อมหน้าจอก็ได้

            Spacer(modifier = Modifier.height(24.dp))

            // Section 2: แชร์
            Text(
                "แชร์",
                color = WealthVaultBrown,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            ShareSection(shareInfo)

            // เผื่อช่องว่างด้านล่างสุดให้เลื่อนพ้นปุ่ม
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
@Composable
fun SummaryCard( data: LiabilityModel) {
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
            SummaryRow("ชื่อ",data.name )
            SummaryRow("ชนิด",data.type )
            SummaryRow("จำนวน",data.principal.toString())
            SummaryRow("ดอกเบี้ย", data.interestRate)
            SummaryRow("วันที่เริ่มต้น",data.startedAt)
            SummaryRow("วันที่สิ้นสุด",data.endedAt)
            SummaryRow("ผู้รับผิดชอบ",data.creditor)
            SummaryRow("คำอธิบาย", data.description)

            Spacer(modifier = Modifier.height(16.dp))
            Text("ข้อมูลอ้างอิง", color = WealthVaultBrown, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            // ส่วนรูปภาพอ้างอิง: ใช้ LazyRow เพื่อให้เลื่อนซ้าย-ขวาได้ด้วย
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {

                val images = data.attachments

                items(images) { img ->
                    // 💡 1. ปรับการเช็ก PDF ให้ชัวร์ขึ้น (เช็กตัวเล็ก/ใหญ่ และเช็ก Type ให้ตรงเป๊ะ)
                    val isPdf = img.name.lowercase().endsWith(".pdf") || img.type.name.equals("PDF", ignoreCase = true)

                    // 🔍 2. พิมพ์ Log ออกมาดูเลยว่าแต่ละไฟล์ที่กดเลือกมา มันได้ค่าอะไร
                    println("🖼 Preview Check -> Name: ${img.name}, Type: ${img.type}, isPdf: $isPdf")

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFFFF8F3))
                            .border(1.dp, Color(0xFFF3E9D8), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isPdf) {
                            // 📄 โชว์ UI ของ PDF
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PictureAsPdf,
                                    contentDescription = "PDF File",
                                    tint = Color(0xFFE57373),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = img.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.DarkGray,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        } else {
                            // 🖼 โชว์ UI ของรูปภาพ
                            val imageData = img.platformData as? ByteArray

                            if (imageData != null && imageData.isNotEmpty()) {
                                AsyncImage(
                                    model = imageData,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                // ถ้ารูปพัง หรือ platformData เป็น null ให้โชว์ไอคอนนี้
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = Color(0xFFD7CCC8),
                                    modifier = Modifier.padding(24.dp)
                                )
                            }
                        }
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
fun ShareSection(
    shareInfo: ShareTo? = null,
) {
    // If shareInfo is null, we can return early or show a placeholder
    if (shareInfo == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8F3)),
        border = BorderStroke(1.dp, Color(0xFFF3E5D8))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // เช็คว่าข้อมูลทุกอย่างว่างเปล่าหรือไม่
            val isEmpty = shareInfo.friend.isEmpty() &&
                    shareInfo.group.isEmpty() &&
                    shareInfo.email.isEmpty()

            if (isEmpty) {
                // --- แสดงเมื่อไม่มีข้อมูล ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp), // เพิ่ม space บนล่างให้ดูไม่โล่งไป
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ไม่มีการแชร์ข้อมูล",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            } else {
                // --- ส่วนการ Render ข้อมูลเดิม ---

                // 1. Render Friends
                shareInfo.friend.forEach { friend ->
                    ShareItemCard(name = friend.name ?: "", date = friend.date ?: "")
                }

                // 2. Render Groups
                shareInfo.group.forEach { group ->
                    ShareItemCard(name = group.name ?: "", groupCount = "5", date = group.date ?: "")
                }

                // Divider (แสดงเฉพาะเมื่อมี Email และมีข้อมูลด้านบน)
                if (shareInfo.email.isNotEmpty() && (shareInfo.friend.isNotEmpty() || shareInfo.group.isNotEmpty())) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = Color(0xFF8B4513).copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }

                // 3. Render Emails
                shareInfo.email.forEach { email ->
                    ShareItemCard(
                        name = email.name ?: "",
                        isEmail = true,
                        date = email.date ?: ""
                    )
                }
            }
        }
    }
}
