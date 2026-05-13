package com.wealthvault_final.`financial-asset`.ui.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.wealthvault.core.generated.resources.Res
import com.wealthvault.core.generated.resources.bookbank
import com.wealthvault.core.generated.resources.cashgold
import com.wealthvault.core.generated.resources.ic_common_back
import com.wealthvault.core.generated.resources.insurance
import com.wealthvault.core.generated.resources.land
import com.wealthvault.core.generated.resources.stock
import com.wealthvault.core.theme.LightBg
import com.wealthvault.core.theme.LightPrimary
import com.wealthvault.core.theme.LightText
import com.wealthvault.core.utils.getScreenModel
import com.wealthvault_final.`financial-asset`.ui.bankaccount.BankAccountFormScreen
import com.wealthvault_final.`financial-asset`.ui.cash.CashFormScreen
import com.wealthvault_final.`financial-asset`.ui.insurance.InsuranceFormScreen
import com.wealthvault_final.`financial-asset`.ui.realestate.RealEstateScreen
import com.wealthvault_final.`financial-asset`.ui.stock.StockFormScreen
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

// 🌟 1. สร้าง ScreenModel เพื่อเก็บสถานะการเลือก
class MenuScreenModel : ScreenModel {
    var selectedId by mutableStateOf<Int?>(1)
}

data class AssetCategory(
    val id: Int,
    val title: String,
    val iconRes: DrawableResource,
    val backgroundColor: Color,
    val borderColor: Color
)

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
        // 🌟 2. เรียกใช้ ScreenModel
        val screenModel = getScreenModel<MenuScreenModel>()

        MenuContent(
            selectedId = screenModel.selectedId,
            onCategorySelect = { screenModel.selectedId = it },
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
    selectedId: Int?,
    onCategorySelect: (Int?) -> Unit,
    onBackClick: () -> Unit = {},
    onNextClick: (AssetCategory?) -> Unit = {}
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = LightBg,
        topBar = {
            Column(modifier = Modifier.statusBarsPadding()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 16.dp, top = 24.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_common_back),
                        contentDescription = "Back",
                        tint = LightPrimary,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { onBackClick() }
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "ประเภททรัพย์สิน",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightPrimary
                    )
                }
            }
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(24.dp)) {
                Button(
                    onClick = { onNextClick(assetCategories.find { it.id == selectedId }) },
                    modifier = Modifier.fillMaxWidth().height(46.dp), // 🌟 มาตรฐาน 46.dp
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LightPrimary),
                    enabled = selectedId != null
                ) {
                    Text("ต่อไป", color = Color.White, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
        ) {
            items(assetCategories) { category ->
                AssetCard(
                    category = category,
                    isSelected = selectedId == category.id,
                    onClick = { onCategorySelect(category.id) }
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
    val borderWidth = if (isSelected) 2.dp else 1.dp
    // 🌟 ปรับสีขอบตอนไม่เลือกให้เป็น LightBorder จางๆ ตามหน้าอื่นๆ
    val borderColor = if (isSelected) category.borderColor else Color.LightGray.copy(alpha = 0.3f)

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(borderWidth, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) category.backgroundColor else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
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
                        .size(20.dp)
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
                    modifier = Modifier.size(70.dp), // ปรับขนาดไอคอนเล็กน้อยให้สมดุล
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = category.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) category.borderColor else LightText,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}