package com.wealthvault.financiallist.ui.asset

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.MockItemCard
import com.wealthvault.financiallist.ui.component.RealItemCard

@Composable
fun AssetScreen(
    onAddClick: () -> Unit // เอาไว้กดปุ่มบวก
) {
    var searchQuery by remember { mutableStateOf("") }

    // โทนสีส้มอิฐ สำหรับหน้าทรัพย์สิน (อิงจากภาพ)
    val assetThemeColor = Color(0xFFD69A6E)

    FinancialListTemplate(
        headerTitle = "ทรัพย์สิน", // หรือจะเว้นว่างไว้ก็ได้ถ้ารูปดีไซน์ไม่มีตัวหนังสือ
        themeColor = assetThemeColor,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        onAddClick = onAddClick,
        headerIcon = {
//            Icon(
//                imageVector = Icons.Default.AccountBalanceWallet,
//                contentDescription = "Asset",
//                tint = assetThemeColor
//            )
        }
    ) {
        LazyColumn {
            item {
                ExpandableCategoryCard(title = "บัญชี", itemCount = 2, themeColor = assetThemeColor, initiallyExpanded = true) {
                    RealItemCard(title = "บัญชีให้ลูก", bank = "SCB", amount = "30,000 บาท")
                    MockItemCard() // กล่องว่างจำลองตามดีไซน์
                }
            }
            item {
                ExpandableCategoryCard(title = "ลงทุน หุ้น กองทุน", itemCount = 2, themeColor = assetThemeColor, initiallyExpanded = true) {
                    MockItemCard()
                    MockItemCard()
                }
            }
            item {
                ExpandableCategoryCard(title = "ประกันชีวิต", itemCount = 2, themeColor = assetThemeColor) {
                    MockItemCard()
                }
            }
            item {
                ExpandableCategoryCard(title = "ที่ดิน", itemCount = 1, themeColor = assetThemeColor) {
                    MockItemCard()
                }
            }

            // ดันให้มีพื้นที่ว่างด้านล่างสุด เลื่อนจอแล้วปุ่มจะได้ไม่บังการ์ด
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}