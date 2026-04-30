package com.wealthvault_final.`financial-asset`.ui.menu

// 🌟 Import Theme และ Resources

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.ArrowBack
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
import com.wealthvault.core.generated.resources.bookbank
import com.wealthvault.core.generated.resources.cashgold
import com.wealthvault.core.generated.resources.insurance
import com.wealthvault.core.generated.resources.land
import com.wealthvault.core.generated.resources.stock
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import com.wealthvault_final.`financial-asset`.ui.bankaccount.BankAccountFormScreen
import com.wealthvault_final.`financial-asset`.ui.cash.CashFormScreen
import com.wealthvault_final.`financial-asset`.ui.insurance.InsuranceFormScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.RealEstateScreen
import com.wealthvault_final.`financial-asset`.ui.stock.StockFormScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

data class AssetCategory(
    val id: Int,
    val title: String,
    val iconRes: DrawableResource,
    val backgroundColor: Color,
    val borderColor: Color
)

// 🌟 คง UI Card แบบเดิมตามที่ระบุ
val assetCategories = listOf(
    AssetCategory(1, "บัญชีเงินฝาก", Res.drawable.bookbank, Color(0xFFE3F2FD), Color(0xFF2196F3)),
    AssetCategory(2, "เงินสด ทองคำ", Res.drawable.cashgold, Color(0xFFF1F8E9), Color(0xFF8BC34A)),
    AssetCategory(3, "ลงทุน หุ้น กองทุน", Res.drawable.stock, Color(0xFFE8F5E9), Color(0xFF4CAF50)),
    AssetCategory(4, "ประกัน", Res.drawable.insurance, Color(0xFFF3E5F5), Color(0xFF9C27B0)),
    AssetCategory(5, "อสังหาริมทรัพย์", Res.drawable.land, Color(0xFFFFF3E0), Color(0xFFFF9800))
)

class MenuScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        MenuContent(
            onBackClick = { navigator.pop() },
            onNextClick = { selectedCategory ->
                when (selectedCategory?.id) {
                    1 -> navigator.push(BankAccountFormScreen())
                    2 -> navigator.push(CashFormScreen())
                    3 -> navigator.push(StockFormScreen())
                    4 -> navigator.push(InsuranceFormScreen())
                    5 -> navigator.push(RealEstateScreen())
                }
            }
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun MenuContent(
    onBackClick: () -> Unit = {},
    onNextClick: (AssetCategory?) -> Unit = {}
) {
    var selectedId by remember { mutableStateOf<Int?>(1) }

    Scaffold(
        modifier = Modifier.fillMaxSize(), // 🌟 ลบ Padding ตรงนี้ออกเพื่อแก้ขอบขาว
        containerColor = LightBg,
        topBar = {
            // 🌟 ใส่ statusBarsPadding ที่ TopBar แทน
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = LightPrimary)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ประเภททรัพย์สิน",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            // 🌟 ใส่ navigationBarsPadding ที่ปุ่ม เพื่อให้เว้นระยะจากขอบล่างพอดี
            Box(modifier = Modifier.navigationBarsPadding().padding(16.dp)) {
                Button(
                    onClick = { onNextClick(assetCategories.find { it.id == selectedId }) },
                    modifier = Modifier.fillMaxWidth().height(50.dp), // 🌟 สูง 50.dp ตามสั่ง
                    shape = RoundedCornerShape(12.dp), // 🌟 โค้ง 12.dp ตามสั่ง
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    enabled = selectedId != null
                ) {
                    Text("ต่อไป", color = Color.White, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // 🌟 ใช้ paddingValues เพื่อไม่ให้ทับกับ Top/Bottom bar
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
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
                        .padding(12.dp)
                        .size(22.dp)
                )
            }

            Column(
                modifier = Modifier.fillMaxSize().padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(category.iconRes),
                    contentDescription = category.title,
                    modifier = Modifier.size(100.dp),
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = category.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = LightText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}