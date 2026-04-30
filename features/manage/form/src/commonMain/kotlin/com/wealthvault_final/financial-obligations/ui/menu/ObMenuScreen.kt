package com.wealthvault_final.`financial-obligations`.ui.menu

// 🌟 Import ธีมของแอป

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.debtpic
import com.wealthvault.core.generated.resources.expensepic
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import com.wealthvault_final.`financial-obligations`.ui.expense.ExpenseFormScreen
import com.wealthvault_final.`financial-obligations`.ui.liability.LiabilityFormScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

// 🌟 ลบสี Hardcode ออก ใช้ของ Theme แทน

data class AssetCategory(
    val id: Int,
    val title: String,
    val iconRes: DrawableResource,
    val backgroundColor: Color,
    val borderColor: Color
)

// 🌟 คงดีไซน์ Card ไว้เหมือนเดิมเป๊ะ
val assetCategories = listOf(
    AssetCategory(1, "หนี้สิน", Res.drawable.debtpic, Color(0xFFE3F2FD), Color(0xFF2196F3)),
    AssetCategory(2, "ค่าใช้จ่ายต่อเนื่อง", Res.drawable.expensepic, Color(0xFFF1F8E9), Color(0xFF8BC34A)),
)

class ObMenuScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        MenuScreen(
            onBackClick = { navigator.pop() },
            onNextClick = { selectedCategory ->
                // 🌟 แก้ไขคอมเมนต์ให้ถูกต้อง จะได้ไม่งงกับฝั่ง Asset
                when (selectedCategory?.id) {
                    1 -> navigator.push(LiabilityFormScreen()) // ประเภทที่ 1: หนี้สิน
                    2 -> navigator.push(ExpenseFormScreen())   // ประเภทที่ 2: ค่าใช้จ่ายต่อเนื่อง
                    else -> {
                        println("กรุณาเลือกประเภทรายการ")
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MenuScreen(
    onBackClick: () -> Unit = {},
    onNextClick: (AssetCategory?) -> Unit = {}
) {
    var selectedId by remember { mutableStateOf<Int?>(1) }

    Scaffold(
        // 🌟 แก้ไขตรงนี้: ลบ statusBarsPadding และ navigationBarsPadding ออก
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            // 🌟 ย้ายมาใส่ statusBarsPadding ตรงนี้แทน เพื่อให้ TopBar เว้นระยะจากขอบบน
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_common_back),
                            contentDescription = "Back",
                            tint = LightPrimary,
                            modifier = Modifier
                                .size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ประเภทหนี้สิน",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 🌟 ย้ายมาใส่ navigationBarsPadding ตรงนี้ เพื่อให้ปุ่มเว้นระยะจากขอบล่าง (แถบ Home)
            Box(modifier = Modifier.navigationBarsPadding().padding(16.dp)) {
                Button(
                    onClick = { onNextClick(assetCategories.find { it.id == selectedId }) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    enabled = selectedId != null
                ) {
                    Text("ต่อไป", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        // ✅ ใช้ paddingValues ที่ได้จาก Scaffold เพื่อจัดการพื้นที่ตรงกลาง
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(paddingValues) // จัดการพื้นที่ที่เหลือระหว่าง TopBar และ BottomBar
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(assetCategories) { category ->
                AssetCard(
                    category = category,
                    isSelected = selectedId == category.id,
                    onClick = { selectedId = category.id }
                )
            }
        }
    }
}

// 🌟 ส่วนนี้คือ UI Card ที่คงไว้เหมือนเดิม 100%
@OptIn(ExperimentalResourceApi::class)
@Composable
fun AssetCard(
    category: AssetCategory,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderWidth = if (isSelected) 2.dp else 0.dp
    val borderColor = if (isSelected) category.borderColor else Color.Transparent

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(containerColor = category.backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = category.borderColor,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(category.iconRes),
                    contentDescription = category.title,
                    modifier = Modifier.size(116.dp),
                )

                Text(
                    text = category.title,
                    style = MaterialTheme.typography.bodyLarge, // 🌟 ปรับให้ใช้ Typography
                    fontWeight = FontWeight.Medium,
                    color = LightText, // 🌟 ใช้ LightText จาก Theme แทน Color.Black
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}