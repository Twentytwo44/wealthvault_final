package com.wealthvault.financiallist.ui.debt

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.wealthvault.financiallist.ui.FinancialListTemplate
import com.wealthvault.financiallist.ui.component.ExpandableCategoryCard
import com.wealthvault.financiallist.ui.component.MockItemCard

@Composable
fun DebtScreen(
    onAddClick: () -> Unit // เอาไว้กดปุ่มบวก
) {
    var searchQuery by remember { mutableStateOf("") }

    // โทนสีแดงแดงเข้ม สำหรับหน้าหนี้สิน (อิงจากภาพ)
    val debtThemeColor = Color(0xFFC44D44)

    FinancialListTemplate(
        headerTitle = "หนี้สิน & รายจ่ายระยะยาว",
        themeColor = debtThemeColor,
        searchQuery = searchQuery,
        onSearchChange = { searchQuery = it },
        onAddClick = onAddClick,
        headerIcon = {
//            Icon(
//                imageVector = Icons.Default.AccountBalance, // ไอคอนธนาคาร/อาคาร
//                contentDescription = "Debt",
//                tint = debtThemeColor
//            )
        }
    ) {
        LazyColumn {
            item {
                ExpandableCategoryCard(
                    title = "รายจ่ายระยะยาว",
                    itemCount = 2,
                    themeColor = debtThemeColor,
                    initiallyExpanded = true
                ) {
                    MockItemCard()
                    MockItemCard()
                }
            }
            item {
                ExpandableCategoryCard(
                    title = "หนี้สิน",
                    itemCount = 2,
                    themeColor = debtThemeColor,
                    initiallyExpanded = true
                ) {
                    MockItemCard()
                    MockItemCard()
                }
            }

            // ดันให้มีพื้นที่ว่างด้านล่างสุด เลื่อนจอแล้วปุ่มจะได้ไม่บังการ์ด
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}