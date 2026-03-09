package com.wealthvault.social.ui.manage_shared

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// --- Import ชิ้นส่วนจากโฟลเดอร์ components/space ---
import com.wealthvault.social.ui.components.space.SharedAssetItem
import com.wealthvault.social.ui.components.space.SpaceTopBar

@Composable
fun SharedAssetManageScreen(
    targetName: String = "Twentytwo",
    onBackClick: () -> Unit
) {
    val themeColor = Color(0xFFC27A5A)
    Scaffold(
        containerColor = Color(0xFFFFF8F3) // สีพื้นหลัง
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SpaceTopBar(title = "การจัดการทรัพย์สิน", onBackClick = onBackClick)
            HorizontalDivider(color = themeColor.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(24.dp))

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                // ส่วนที่ 1: เราแชร์ให้เขา
                item {
                    Text(
                        text = "ทรัพย์สินที่คุณแชร์",
                        fontSize = 16.sp,
                        color = themeColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SharedAssetItem(
                        name = "เก็บเงินซื้อเกม",
                        tag = "บัญชีเงินฝาก",
                        showDelete = true,
                        themeColor = themeColor
                    )
                    SharedAssetItem(
                        name = "ซื้อหุ้นเพื่อรวย",
                        tag = "บัญชีเงินฝาก",
                        showDelete = true,
                        themeColor = themeColor
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // ส่วนที่ 2: เขาแชร์ให้เรา
                item {
                    Text(
                        text = "ทรัพย์สินที่ $targetName แชร์กับคุณ",
                        fontSize = 16.sp,
                        color = themeColor,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    SharedAssetItem(
                        name = "บัญชีเงินฝากเพื่อเกษียณ...",
                        tag = "บัญชีเงินฝาก",
                        showDelete = false,
                        themeColor = themeColor
                    )
                    SharedAssetItem(
                        name = "บัญชีเงินเก็บเพื่อหลาน",
                        tag = "บัญชีเงินฝาก",
                        showDelete = false,
                        themeColor = themeColor
                    )
                }
            }
        }
    }
}